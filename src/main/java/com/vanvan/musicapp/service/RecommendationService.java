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
            // Chỉ lấy dữ liệu cần thiết
            List<ListeningCount> allListeningCounts = listeningCountRepository.findByUserIdIn(
                    Stream.concat(
                            Stream.of(userId),
                            favoriteRepository.findAll().stream().map(fav -> fav.getUser().getId())
                    ).collect(Collectors.toSet())
            );
            List<Favorite> allFavorites = favoriteRepository.findByUserIdIn(
                    Stream.concat(
                            Stream.of(userId),
                            allListeningCounts.stream().map(ListeningCount::getUserId)
                    ).collect(Collectors.toSet())
            );

            Map<Integer, Map<String, Double>> songVectors = songVectorService.getSongVectors();

            // Lấy danh sách bài hát user đã tương tác
            Set<Integer> targetUserSongIds = new HashSet<>();
            allListeningCounts.stream()
                    .filter(lc -> lc.getUserId().equals(userId))
                    .forEach(lc -> targetUserSongIds.add(lc.getSongId()));
            allFavorites.stream()
                    .filter(fav -> fav.getUser().getId().equals(userId))
                    .forEach(fav -> targetUserSongIds.add(fav.getSong().getId()));

            // Tạo vector người dùng mục tiêu (chuẩn hóa)
            Map<String, Double> targetUserVector = new HashMap<>();
            for (Integer songId : targetUserSongIds) {
                Map<String, Double> songVec = songVectors.get(songId);
                if (songVec != null) {
                    for (Map.Entry<String, Double> e : songVec.entrySet()) {
                        targetUserVector.merge(e.getKey(), e.getValue() / targetUserSongIds.size(), Double::sum);
                    }
                }
            }

            // Lấy các người dùng khác
            Set<Integer> otherUsers = new HashSet<>();
            allListeningCounts.forEach(lc -> otherUsers.add(lc.getUserId()));
            allFavorites.forEach(fav -> otherUsers.add(fav.getUser().getId()));
            otherUsers.remove(userId);

            // Tính độ tương đồng
            Map<Integer, Double> userSimilarity = new HashMap<>();
            for (Integer otherUserId : otherUsers) {
                Set<Integer> otherUserSongIds = new HashSet<>();
                allListeningCounts.stream()
                        .filter(lc -> lc.getUserId().equals(otherUserId))
                        .forEach(lc -> otherUserSongIds.add(lc.getSongId()));
                allFavorites.stream()
                        .filter(fav -> fav.getUser().getId().equals(otherUserId))
                        .forEach(fav -> otherUserSongIds.add(fav.getSong().getId()));

                Map<String, Double> otherUserVector = new HashMap<>();
                for (Integer songId : otherUserSongIds) {
                    Map<String, Double> songVec = songVectors.get(songId);
                    if (songVec != null) {
                        for (Map.Entry<String, Double> e : songVec.entrySet()) {
                            otherUserVector.merge(e.getKey(), e.getValue() / otherUserSongIds.size(), Double::sum);
                        }
                    }
                }

                double similarity = songVectorService.calculatePearsonCorrelation(targetUserVector, otherUserVector);
                userSimilarity.put(otherUserId, similarity);
            }

            // Lấy top 5 user tương tự (chỉ lấy similarity dương)
            List<Integer> similarUsers = userSimilarity.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .toList();

            // Lấy bài hát gợi ý từ các user tương tự
            Set<Integer> interactedSongs = new HashSet<>(targetUserSongIds);
            Set<Integer> recommendedSongIds = new HashSet<>();

            for (Integer similarUserId : similarUsers) {
                allListeningCounts.stream()
                        .filter(lc -> lc.getUserId().equals(similarUserId))
                        .map(ListeningCount::getSongId)
                        .filter(songId -> !interactedSongs.contains(songId))
                        .forEach(recommendedSongIds::add);

                allFavorites.stream()
                        .filter(fav -> fav.getUser().getId().equals(similarUserId))
                        .map(fav -> fav.getSong().getId())
                        .filter(songId -> !interactedSongs.contains(songId))
                        .forEach(recommendedSongIds::add);
            }

            // Trả về danh sách gợi ý
            List<SongResponse> recommendedSongs = recommendedSongIds.stream()
                    .map(songId -> songRepository.findById(songId).orElse(null))
                    .filter(Objects::nonNull)
                    .map(utilsService::convertToSongResponse)
                    .limit(limit)
                    .toList();

            // Bổ sung bài hát phổ biến nếu thiếu
            if (recommendedSongs.size() < limit) {
                List<Object[]> topSongs = listeningCountRepository.findTopSongsByListenCount();
                List<SongResponse> popularSongs = topSongs.stream()
                        .map(obj -> (Integer) obj[0])
                        .map(songId -> songRepository.findById(songId).orElse(null))
                        .filter(Objects::nonNull)
                        .filter(song -> !interactedSongs.contains(song.getId()))
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