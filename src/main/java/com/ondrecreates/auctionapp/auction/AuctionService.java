package com.ondrecreates.auctionapp.auction;

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

    @Transactional
    public Auction create(Auction auction) {
        auction.setCurrentPrice(auction.getStartingPrice());
        auction.setStatus(AuctionStatus.ACTIVE);
        return auctionRepository.save(auction);
    }

    public Auction getById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException(id));
    }

    public List<Auction> getAll() {
        return auctionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
