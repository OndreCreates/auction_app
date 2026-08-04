package com.ondrecreates.auctionapp.auction;

import com.ondrecreates.auctionapp.category.CategoryNotFoundException;
import com.ondrecreates.auctionapp.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionImageRepository auctionImageRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Auction create(Auction auction, List<String> imageUrls) {
        if (!categoryRepository.existsById(auction.getCategoryId())) {
            throw new CategoryNotFoundException(auction.getCategoryId());
        }

        auction.setCurrentPrice(auction.getStartingPrice());
        auction.setStatus(AuctionStatus.ACTIVE);
        Auction savedAuction = auctionRepository.save(auction);

        for (int i = 0; i < imageUrls.size(); i++) {
            auctionImageRepository.save(AuctionImage.builder()
                    .auctionId(savedAuction.getId())
                    .url(imageUrls.get(i))
                    .sortOrder(i)
                    .build());
        }

        return savedAuction;
    }

    public Auction getById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException(id));
    }

    public List<Auction> getAll(String categorySlug) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (categorySlug == null) {
            return auctionRepository.findAll(sort);
        }

        Long categoryId = categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new CategoryNotFoundException(categorySlug))
                .getId();
        return auctionRepository.findByCategoryId(categoryId, sort);
    }
}
