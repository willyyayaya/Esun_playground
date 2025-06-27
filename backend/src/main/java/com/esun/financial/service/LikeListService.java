package com.esun.financial.service;

import com.esun.financial.dto.LikeListRequest;
import com.esun.financial.dto.LikeListResponse;
import com.esun.financial.entity.LikeList;
import com.esun.financial.entity.Product;
import com.esun.financial.repository.LikeListRepository;
import com.esun.financial.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 喜好清單服務類別
 * 
 * @author Willy Weng
 * @version 1.0.0
 */
@Service
@Transactional
public class LikeListService {

    private static final Logger log = LoggerFactory.getLogger(LikeListService.class);
    
    private final LikeListRepository likeListRepository;
    private final ProductRepository productRepository;

    public LikeListService(LikeListRepository likeListRepository, ProductRepository productRepository) {
        this.likeListRepository = likeListRepository;
        this.productRepository = productRepository;
    }

    /**
     * 查詢使用者喜好清單
     */
    @Transactional(readOnly = true)
    public List<LikeListResponse> getUserLikeList(String userId) {
        log.info("查詢使用者 {} 的喜好清單", userId);
        
        List<LikeList> likeLists = likeListRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return likeLists.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 新增喜好商品
     */
    public LikeListResponse addLikeList(LikeListRequest request) {
        log.info("新增喜好商品: {}", request);
        
        // 檢查產品編號是否提供
        if (request.getProductNo() == null) {
            throw new IllegalArgumentException("新增喜好商品時產品編號不能為空");
        }
        
        // 檢查產品是否存在
        Product product = productRepository.findById(request.getProductNo())
                .orElseThrow(() -> new IllegalArgumentException("產品不存在: " + request.getProductNo()));

        // 檢查是否已經存在相同的喜好商品
        Optional<LikeList> existing = likeListRepository.findByUserIdAndProductNo(
                request.getUserId(), request.getProductNo());
        
        if (existing.isPresent()) {
            throw new IllegalArgumentException("此商品已在喜好清單中，請直接修改數量");
        }

        // 計算總金額和手續費
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(request.getOrderQuantity()));
        BigDecimal totalFee = totalAmount.multiply(product.getFeeRate());

        // 建立喜好清單項目
        LikeList likeList = new LikeList();
        likeList.setUserId(request.getUserId());
        likeList.setProductNo(request.getProductNo());
        likeList.setOrderQuantity(request.getOrderQuantity());
        likeList.setAccount(request.getAccount());
        likeList.setTotalAmount(totalAmount);
        likeList.setTotalFee(totalFee);

        LikeList saved = likeListRepository.save(likeList);
        log.info("成功新增喜好商品: SN={}", saved.getSn());
        
        return convertToResponse(saved);
    }

    /**
     * 更新喜好商品
     */
    public LikeListResponse updateLikeList(Integer sn, LikeListRequest request) {
        log.info("更新喜好商品: SN={}, Request={}", sn, request);
        
        LikeList likeList = likeListRepository.findById(sn)
                .orElseThrow(() -> new IllegalArgumentException("喜好商品不存在: " + sn));

        // 取得產品資訊以重新計算
        Product product = productRepository.findById(likeList.getProductNo())
                .orElseThrow(() -> new IllegalArgumentException("產品不存在: " + likeList.getProductNo()));

        // 重新計算總金額和手續費
        BigDecimal totalAmount = product.getPrice()
                .multiply(BigDecimal.valueOf(request.getOrderQuantity()));
        BigDecimal totalFee = totalAmount.multiply(product.getFeeRate());

        // 更新數據
        likeList.setOrderQuantity(request.getOrderQuantity());
        likeList.setAccount(request.getAccount());
        likeList.setTotalAmount(totalAmount);
        likeList.setTotalFee(totalFee);

        LikeList updated = likeListRepository.save(likeList);
        log.info("成功更新喜好商品: SN={}", updated.getSn());
        
        return convertToResponse(updated);
    }

    /**
     * 刪除喜好商品
     */
    public void deleteLikeList(Integer sn) {
        log.info("刪除喜好商品: SN={}", sn);
        
        if (!likeListRepository.existsById(sn)) {
            throw new IllegalArgumentException("喜好商品不存在: " + sn);
        }

        likeListRepository.deleteById(sn);
        log.info("成功刪除喜好商品: SN={}", sn);
    }

    /**
     * 轉換實體到回應DTO
     */
    private LikeListResponse convertToResponse(LikeList likeList) {
        // 取得產品資訊
        Product product = productRepository.findById(likeList.getProductNo()).orElse(null);
        
        LikeListResponse response = new LikeListResponse();
        response.setSn(likeList.getSn());
        response.setUserId(likeList.getUserId());
        response.setProductNo(likeList.getProductNo());
        response.setOrderQuantity(likeList.getOrderQuantity());
        response.setAccount(likeList.getAccount());
        response.setTotalFee(likeList.getTotalFee());
        response.setTotalAmount(likeList.getTotalAmount());
        response.setCreatedAt(likeList.getCreatedAt());
        response.setUpdatedAt(likeList.getUpdatedAt());
        
        // 設定產品資訊
        if (product != null) {
            response.setProductName(product.getProductName());
            response.setPrice(product.getPrice());
            response.setFeeRate(product.getFeeRate());
        }
        
        return response;
    }
} 