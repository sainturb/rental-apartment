package miu.edu.product.services;

import miu.edu.product.models.Review;
import miu.edu.product.repositories.ReviewRepository;
import miu.edu.product.search.ReviewSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository repository;

    @Autowired
    private ReviewSearchRepository searchRepository;

    @Autowired
    private ProductService productService;

    public List<Review> getAll() {
        return repository.findAll();
    }

    public long getCount(Long productId) {
        return repository.countByProductId(productId);
    }

    public List<Review> getByProductId(Long productId) {
        return repository.findAllByProductId(productId);
    }


    public Optional<Review> getById(Long id) {
        return repository.findById(id);
    }

    public Review save(Review review) {
        final Review saved = repository.save(review);
        productService.updateRating(
                review.getProductId(),
                getByProductId(review.getProductId())
        );
        searchRepository.save(saved);
        return saved;
    }

    public void delete(Long id) {
        searchRepository.deleteById(id);
        repository.deleteById(id);
    }
}
