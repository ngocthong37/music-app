package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Favorite;
import com.vanvan.musicapp.entity.ListeningCount;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.repository.FavoriteRepository;
import com.vanvan.musicapp.repository.ListeningCountRepository;
import com.vanvan.musicapp.repository.SongRepository;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.response.SongResponse;
import com.vanvan.musicapp.utils.UtilsService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class RecommendationService {

    private final SongRepository songRepository;

    private final FavoriteRepository favoriteRepository;

    private final ListeningCountRepository listeningCountRepository;

    private final SongVectorService songVectorService;

    private final UtilsService utilsService;

    // Khởi tạo vector TF-IDF khi ứng dụng chạy
    @PostConstruct
    public void init() throws IOException {
        List<Song> songs = songRepository.findAll();
        songVectorService.buildVectors(songs);
    }

    // Gợi ý bài hát dựa trên Content-based Filtering
    public ResponseObject getContentBasedRecommendations(Integer userId, int limit) {
        try {
            List<Favorite> favorites = favoriteRepository.findByUserId(userId);
            List<ListeningCount> listeningCounts = listeningCountRepository.findByUserId(userId);

            Set<Integer> interactedSongIds = new HashSet<>();
            favorites.forEach(fav -> interactedSongIds.add(fav.getSong().getId()));
            listeningCounts.forEach(count -> interactedSongIds.add(count.getSongId()));

            if (interactedSongIds.isEmpty()) {
                List<Object> randomSongs = songRepository.findAll().stream()
                        .limit(limit)
                        .map(utilsService::convertToSongResponse)
                        .collect(Collectors.toList());
                return new ResponseObject("success", "User has no interactions, returning random songs", randomSongs);
            }

            Map<Integer, Map<String, Double>> songVectors = songVectorService.getSongVectors();
            Map<Integer, Double> similarityScores = new HashMap<>();

            List<Song> allSongs = songRepository.findAll();
            for (Song song : allSongs) {
                if (interactedSongIds.contains(song.getId())) continue;

                double totalSimilarity = 0.0;
                int validComparisons = 0;

                for (Integer interactedSongId : interactedSongIds) {
                    Map<String, Double> v1 = songVectors.get(interactedSongId);
                    Map<String, Double> v2 = songVectors.get(song.getId());
                    if (v1 != null && v2 != null) {
                        totalSimilarity += songVectorService.calculateCosineSimilarity(v1, v2);
                        validComparisons++;
                    }
                }

                if (validComparisons > 0) {
                    similarityScores.put(song.getId(), totalSimilarity / validComparisons);
                }
            }

            List<Object> recommendedSongs = similarityScores.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(entry -> songRepository.findById(entry.getKey()).orElse(null))
                    .filter(Objects::nonNull)
                    .map(utilsService::convertToSongResponse)
                    .collect(Collectors.toList());

            return new ResponseObject("success", "Content-based recommendations fetched successfully", recommendedSongs);

        } catch (Exception e) {
            return new ResponseObject("error", "Failed to get content-based recommendations: " + e.getMessage(), null);
        }
    }

    public ResponseObject getCollaborativeRecommendations(Integer userId, int limit) {
        try {
            // 1. Lấy toàn bộ dữ liệu cần thiết
            Set<Integer> allUserIds = new HashSet<>();
            allUserIds.add(userId);
            allUserIds.addAll(favoriteRepository.findAll().stream().map(fav -> fav.getUser().getId()).collect(Collectors.toSet()));
            List<ListeningCount> allListeningCounts = listeningCountRepository.findByUserIdIn(allUserIds);
            List<Favorite> allFavorites = favoriteRepository.findByUserIdIn(allUserIds);

            // 2. Lấy vector bài hát (TF-IDF)
            Map<Integer, Map<String, Double>> songVectors = songVectorService.getSongVectors();

            // 3. Tạo rating ngầm cho user mục tiêu
            Map<Integer, Double> targetRatingMap = buildUserRatingMap(userId, allListeningCounts, allFavorites);
            Map<String, Double> targetUserVector = buildWeightedUserVector(targetRatingMap, songVectors);

            // 4. Tìm các user khác
            Set<Integer> otherUsers = allUserIds.stream().filter(id -> !id.equals(userId)).collect(Collectors.toSet());

            // 5. Tính độ tương đồng giữa user hiện tại và các user khác
            Map<Integer, Double> userSimilarity = new HashMap<>();
            for (Integer otherUserId : otherUsers) {
                Map<Integer, Double> otherRatingMap = buildUserRatingMap(otherUserId, allListeningCounts, allFavorites);
                Map<String, Double> otherUserVector = buildWeightedUserVector(otherRatingMap, songVectors);
                double similarity = songVectorService.calculatePearsonCorrelation(targetUserVector, otherUserVector);
                userSimilarity.put(otherUserId, similarity);
            }

            // 6. Chọn top 5 user tương tự
            List<Integer> similarUsers = userSimilarity.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .toList();

            // 7. Lấy danh sách bài hát user đã tương tác
            Set<Integer> interactedSongs = targetRatingMap.keySet();

            // 8. Lấy bài hát từ các user tương tự
            Set<Integer> recommendedSongIds = new HashSet<>();
            for (Integer similarUserId : similarUsers) {
                Map<Integer, Double> similarRatingMap = buildUserRatingMap(similarUserId, allListeningCounts, allFavorites);
                for (Integer songId : similarRatingMap.keySet()) {
                    if (!interactedSongs.contains(songId)) {
                        recommendedSongIds.add(songId);
                    }
                }
            }

            // 9. Trả về danh sách bài hát gợi ý
            List<SongResponse> recommendedSongs = recommendedSongIds.stream()
                    .map(songId -> songRepository.findById(songId).orElse(null))
                    .filter(Objects::nonNull)
                    .map(utilsService::convertToSongResponse)
                    .limit(limit)
                    .toList();

            // 10. Bổ sung bài hát phổ biến nếu thiếu
            if (recommendedSongs.size() < limit) {
                List<Object[]> topSongs = listeningCountRepository.findTopSongsByListenCount();
                List<SongResponse> popularSongs = topSongs.stream()
                        .map(obj -> (Integer) obj[0])
                        .filter(songId -> !interactedSongs.contains(songId))
                        .map(songId -> songRepository.findById(songId).orElse(null))
                        .filter(Objects::nonNull)
                        .map(utilsService::convertToSongResponse)
                        .limit(limit - recommendedSongs.size())
                        .toList();

                recommendedSongs = new ArrayList<>(recommendedSongs);
                recommendedSongs.addAll(popularSongs);
            }

            return new ResponseObject("success", "Recommendations generated successfully", recommendedSongs);

        } catch (Exception e) {
            return new ResponseObject("error", "Failed to generate recommendations: " + e.getMessage(), null);
        }
    }

    // Tạo bản đồ rating ngầm từ listening và favorite
    private Map<Integer, Double> buildUserRatingMap(Integer userId, List<ListeningCount> listeningCounts, List<Favorite> favorites) {
        Map<Integer, Double> ratingMap = new HashMap<>();

        // Gán từ listening count
        listeningCounts.stream()
                .filter(lc -> lc.getUserId().equals(userId))
                .forEach(lc -> {
                    int count = lc.getCount();
                    double rating = (count >= 10) ? 4.0 : (count >= 5) ? 3.0 : (count >= 1) ? 2.0 : 0.0;
                    ratingMap.put(lc.getSongId(), rating);
                });

        // Gán từ favorite (ưu tiên hơn)
        favorites.stream()
                .filter(fav -> fav.getUser().getId().equals(userId))
                .forEach(fav -> ratingMap.put(fav.getSong().getId(), 5.0));

        return ratingMap;
    }

    // Tạo vector người dùng với trọng số dựa trên rating
    private Map<String, Double> buildWeightedUserVector(Map<Integer, Double> ratingMap, Map<Integer, Map<String, Double>> songVectors) {
        Map<String, Double> userVector = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : ratingMap.entrySet()) {
            Integer songId = entry.getKey();
            Double rating = entry.getValue();
            Map<String, Double> songVector = songVectors.get(songId);
            if (songVector == null) continue;

            for (Map.Entry<String, Double> e : songVector.entrySet()) {
                String feature = e.getKey();
                double weighted = e.getValue() * rating;
                userVector.merge(feature, weighted, Double::sum);
            }
        }

        return userVector;
    }


    // Kết hợp cả hai phương pháp
    public ResponseObject getRecommendations(Integer userId, int limit) {
        try {
            ResponseObject contentResponse = getContentBasedRecommendations(userId, limit / 2);
            ResponseObject collaborativeResponse = getCollaborativeRecommendations(userId, limit / 2);

            List<Object> contentBased = contentResponse.getData() instanceof List ? (List<Object>) contentResponse.getData() : new ArrayList<>();
            List<Object> collaborative = collaborativeResponse.getData() instanceof List ? (List<Object>) collaborativeResponse.getData() : new ArrayList<>();

            // Kết hợp và loại bỏ trùng lặp theo ID
            Map<Integer, Object> uniqueMap = new LinkedHashMap<>();
            for (Object song : contentBased) {
                if (song instanceof SongResponse songResp) {
                    uniqueMap.put(songResp.getId(), songResp);
                }
            }
            for (Object song : collaborative) {
                if (song instanceof SongResponse songResp) {
                    uniqueMap.putIfAbsent(songResp.getId(), songResp);
                }
            }

            List<Object> finalList = new ArrayList<>(uniqueMap.values());

            // Bổ sung bài hát phổ biến nếu thiếu
            if (finalList.size() < limit) {
                Set<Integer> existingIds = finalList.stream()
                        .filter(song -> song instanceof SongResponse)
                        .map(song -> ((SongResponse) song).getId())
                        .collect(Collectors.toSet());
                List<Object[]> topSongs = listeningCountRepository.findTopSongsByListenCount();
                List<Object> popularSongs = topSongs.stream()
                        .map(obj -> (Integer) obj[0])
                        .map(songId -> songRepository.findById(songId).orElse(null))
                        .filter(Objects::nonNull)
                        .filter(song -> !existingIds.contains(song.getId()))
                        .map(utilsService::convertToSongResponse)
                        .limit(limit - finalList.size())
                        .collect(Collectors.toList());
                finalList.addAll(popularSongs);
            }

            // Giới hạn danh sách cuối cùng
            finalList = finalList.stream().limit(limit).collect(Collectors.toList());

            // Bọc trong map với key "songs"
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("songs", finalList);

            return new ResponseObject("success", "Combined recommendations fetched successfully", responseData);

        } catch (Exception e) {
            return new ResponseObject("error", "Failed to get combined recommendations: " + e.getMessage(), null);
        }
    }


}