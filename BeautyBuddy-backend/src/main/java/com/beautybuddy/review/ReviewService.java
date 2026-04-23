package com.beautybuddy.review;

import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.entity.ProductShade;
import com.beautybuddy.product.repo.ProductRepository;
import com.beautybuddy.product.repo.ProductShadeRepository;
import com.beautybuddy.config.RedisCacheConfig;
import com.beautybuddy.report.repo.ReviewReportRepository;
import com.beautybuddy.review.dto.DisplayReviewDTO;
import com.beautybuddy.review.dto.SubmitReviewDTO;
import com.beautybuddy.review.entity.Review;
import com.beautybuddy.review.entity.ReviewImage;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.upvote.repo.ReviewUpvoteRepository;
import com.beautybuddy.review.dto.EditReviewDTO;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.Set;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductShadeRepository productShadeRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewUpvoteRepository reviewUpvoteRepository;

    private final Counter reviewCounter;

    public ReviewService(ReviewRepository reviewRepository, ReviewReportRepository reviewReportRepository, UserRepository userRepository, ProductRepository productRepository, ProductShadeRepository productShadeRepository, ReviewUpvoteRepository reviewUpvoteRepository, MeterRegistry meterRegistry) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productShadeRepository = productShadeRepository;
        this.reviewReportRepository = reviewReportRepository;
        this.reviewUpvoteRepository = reviewUpvoteRepository;
        this.reviewCounter = Counter.builder("product_reviews_total")
            .description("Total number of reviews")
            .register(meterRegistry);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_SEARCH_FEED_CACHE, allEntries = true)
    })
    public void addReview(String email, SubmitReviewDTO review) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(review.productId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductShade shade = null;
        if (review.shadeName() != null) {
            shade = productShadeRepository.findByProductAndShadeName(product, review.shadeName())
                .orElseThrow(() -> new RuntimeException("Shade not found"));
        }
        if (!product.getProductShades().isEmpty() && shade == null) {
            shade = productShadeRepository.findByProductAndShadeNumber(product, 1)
                .orElseThrow(() -> new RuntimeException("Default shade not found"));
        }
        BigDecimal rating = review.rating();
        String reviewTitle = review.title();
        String reviewText = review.text();
        List<ReviewImage> reviewImages = review.imageLinks() == null ? List.of() : review.imageLinks().stream()
            .map(link -> {
                ReviewImage img = new ReviewImage();
                img.setImageLink(link);
                img.setCreatedAt(LocalDateTime.now());
                return img;
            })
            .toList();
                    
        Review newReview = new Review();
        newReview.setUser(user);
        newReview.setProduct(product);
        newReview.setProductShade(shade);
        newReview.setRating(rating);
        newReview.setTitle(reviewTitle);
        newReview.setText(reviewText);
        for (ReviewImage img : reviewImages) {
            img.setReview(newReview);
        }
        newReview.setReviewImages(reviewImages);

        reviewRepository.save(newReview);
        reviewCounter.increment();
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_SEARCH_FEED_CACHE, allEntries = true)
    })
    public void editReview(String email, Long reviewId, EditReviewDTO review) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Review existingReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        if (existingReview.getUser().getId() != user.getId()) {
            throw new RuntimeException("User not authorized to edit this review");
        } else {
            ProductShade shade = null;
            if (review.shadeName() != null) {
                shade = productShadeRepository.findByProductAndShadeName(existingReview.getProduct(), review.shadeName())
                    .orElseThrow(() -> new RuntimeException("Shade not found"));
            }
            existingReview.setProductShade(shade);
            existingReview.setRating(review.rating());
            existingReview.setTitle(review.title());
            existingReview.setText(review.text());

            // Handle review images
            List<ReviewImage> newImages = review.imageLinks() == null ? List.of() : review.imageLinks().stream()
                .map(link -> {
                    ReviewImage img = new ReviewImage();
                    img.setImageLink(link);
                    img.setCreatedAt(LocalDateTime.now());
                    img.setReview(existingReview);
                    return img;
                })
                .toList();
            
            // Remove old images
            existingReview.getReviewImages().clear();
            // Add new images
            existingReview.getReviewImages().addAll(newImages);

            reviewRepository.save(existingReview);
        }
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_SEARCH_FEED_CACHE, allEntries = true)
    })
    public void removeReview(String email, Long reviewId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Review existingReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        if (existingReview.getUser().getId() != user.getId()) {
            throw new RuntimeException("User not authorized to delete this review");
        } else {
            existingReview.setDeletedAt(LocalDateTime.now());
            reviewRepository.save(existingReview);
        }
    }

    public BigDecimal getAverageRatingForProduct(Long productId) {
        BigDecimal averageRating = reviewRepository.findAverageRatingByProductId(productId);
        return averageRating != null ? averageRating : BigDecimal.ZERO;
    }

    private PageRequest buildReviewPageRequest(int page, int size, String sortKey) {
        String effectiveSortKey = sortKey == null ? "created_desc" : sortKey;
        Sort sort = switch (effectiveSortKey) {
            case "helpful_desc" -> Sort.by(Sort.Direction.DESC, "upvoteCount");
            case "created_asc" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "rating_desc" -> Sort.by(Sort.Direction.DESC, "rating");
            case "rating_asc" -> Sort.by(Sort.Direction.ASC, "rating");
            case "created_desc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        return PageRequest.of(page, size, sort);
    }

    @Cacheable(
        cacheNames = RedisCacheConfig.REVIEW_FEED_CACHE,
        key = "#productId + ':' + #page + ':' + #size + ':' + T(java.util.Objects).toString(#sortKey, 'created_desc') + ':' + T(java.util.Objects).toString(#shadeFilter, '') + ':' + T(java.util.Objects).toString(#userEmail, 'anonymous')"
    )
    public Page<DisplayReviewDTO> getReviewsForProduct(Long productId, int page, int size, String sortKey, String shadeFilter, String userEmail) {
        final User currentUser = userEmail != null
            ? userRepository.findByEmail(userEmail).orElse(null)
            : null;
        PageRequest pageRequest = buildReviewPageRequest(page, size, sortKey);
        Page<Review> reviewPage;
        if (shadeFilter != null && !shadeFilter.isBlank()) {
            reviewPage = reviewRepository.findByProduct_IdAndProductShade_ShadeNameIgnoreCaseAndDeletedAtIsNullAndApprovedTrue(
                productId,
                shadeFilter,
                pageRequest
            );
        } else {
            reviewPage = reviewRepository.findByProduct_IdAndDeletedAtIsNullAndApprovedTrue(
                productId,
                pageRequest
            );
        }

        Set<Long> reportedReviewIds = currentUser == null
            ? Set.of()
            : reviewReportRepository.findAllByUser(currentUser).stream()
                .map(report -> report.getReview().getId())
                .collect(Collectors.toSet());

        List<Review> filteredReviews = reviewPage.getContent().stream()
            .filter(review -> !reportedReviewIds.contains(review.getId()))
            .toList();

        Page<Review> filteredPage = new PageImpl<>(
            filteredReviews,
            reviewPage.getPageable(),
            filteredReviews.size()
        );
        return filteredPage.map(review -> {
            List<String> imageLinks = review.getReviewImages().stream()
                .map(img -> img.getImageLink())
                .toList();

            String shadeName = review.getProductShade() != null
                ? review.getProductShade().getShadeName()
                : null;

            boolean hasUpvoted = currentUser != null
                && reviewUpvoteRepository.findByUserAndReview(currentUser, review).isPresent();

            return new DisplayReviewDTO(
                review.getId(),
                review.getUser().getUsername(),
                review.getUser().getAvatarLink(),
                review.getRating(),
                review.getCreatedAt(),
                review.getProduct().getId(),
                shadeName,
                review.getTitle(),
                review.getText(),
                imageLinks,
                review.getUpvoteCount(),
                hasUpvoted
            );
        });
    }

    @Cacheable(
        cacheNames = RedisCacheConfig.REVIEW_SEARCH_FEED_CACHE,
        key = "#productId + ':' + T(java.util.Objects).toString(#query, '') + ':' + #page + ':' + #size + ':' + T(java.util.Objects).toString(#sortKey, 'created_desc') + ':' + T(java.util.Objects).toString(#shadeFilter, '') + ':' + T(java.util.Objects).toString(#userEmail, 'anonymous')"
    )
    public Page<DisplayReviewDTO> searchReviewsForProduct(Long productId, String query, int page, int size, String sortKey, String shadeFilter, String userEmail) {
        final User currentUser = userEmail != null
            ? userRepository.findByEmail(userEmail).orElse(null)
            : null;
        PageRequest pageRequest = buildReviewPageRequest(page, size, sortKey);
        Page<Review> reviewPage;
        if (shadeFilter != null && !shadeFilter.isBlank()) {
            reviewPage = reviewRepository.searchByProductAndShadeAndText(
                productId,
                shadeFilter,
                query,
                pageRequest
            );
        } else {
            reviewPage = reviewRepository.searchByProductAndText(
                productId,
                query,
                pageRequest
            );
        }

        Set<Long> reportedReviewIds = currentUser == null
            ? Set.of()
            : reviewReportRepository.findAllByUser(currentUser).stream()
                .map(report -> report.getReview().getId())
                .collect(Collectors.toSet());

        List<Review> filteredReviews = reviewPage.getContent().stream()
            .filter(review -> !reportedReviewIds.contains(review.getId()))
            .toList();

        Page<Review> filteredPage = new PageImpl<>(
            filteredReviews,
            reviewPage.getPageable(),
            filteredReviews.size()
        );
        return filteredPage.map(review -> {
            List<String> imageLinks = review.getReviewImages().stream()
                .map(img -> img.getImageLink())
                .toList();

            String shadeName = review.getProductShade() != null
                ? review.getProductShade().getShadeName()
                : null;

            boolean hasUpvoted = currentUser != null
                && reviewUpvoteRepository.findByUserAndReview(currentUser, review).isPresent();

            return new DisplayReviewDTO(
                review.getId(),
                review.getUser().getUsername(),
                review.getUser().getAvatarLink(),
                review.getRating(),
                review.getCreatedAt(),
                review.getProduct().getId(),
                shadeName,
                review.getTitle(),
                review.getText(),
                imageLinks,
                review.getUpvoteCount(),
                hasUpvoted
            );
        });
    }
}
