package com.esun.financial.repository;

import com.esun.financial.entity.LikeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 喜好清單資料存取介面
 * 使用 JPA 和原生查詢實現資料存取，防止 SQL Injection
 * 
 * @author Willy Weng
 * @version 1.0.0
 */
@Repository
public interface LikeListRepository extends JpaRepository<LikeList, Integer> {

    /**
     * 檢查使用者是否已經喜歡該商品
     * 
     * @param userId 使用者ID
     * @param productNo 產品編號
     * @return 是否存在
     */
    @Query("SELECT l FROM LikeList l WHERE l.userId = :userId AND l.productNo = :productNo")
    Optional<LikeList> findByUserIdAndProductNo(
        @Param("userId") String userId, 
        @Param("productNo") Integer productNo
    );

    /**
     * 根據使用者ID查詢喜好清單
     * 
     * @param userId 使用者ID
     * @return 喜好清單列表
     */
    @Query("SELECT l FROM LikeList l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
    List<LikeList> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);

    /**
     * 根據使用者ID查詢喜好清單（簡化版）
     * 
     * @param userId 使用者ID
     * @return 喜好清單列表
     */
    @Query("SELECT l FROM LikeList l WHERE l.userId = :userId")
    List<LikeList> findByUserId(@Param("userId") String userId);

    /**
     * 查詢用戶喜好清單詳細資訊 (JOIN 查詢)
     * 
     * @param userId 使用者ID
     * @return 喜好清單詳細資訊
     */
    @Query(value = "SELECT " +
            "ll.SN, ll.UserID, u.UserName, u.Email, " +
            "ll.ProductNo, p.ProductName, p.Price, p.FeeRate, " +
            "ll.OrderQuantity, ll.Account, ll.TotalFee, ll.TotalAmount, " +
            "ll.CreatedAt, ll.UpdatedAt " +
            "FROM LikeList ll " +
            "INNER JOIN User u ON ll.UserID = u.UserID " +
            "INNER JOIN Product p ON ll.ProductNo = p.No " +
            "WHERE ll.UserID = :userId " +
            "ORDER BY ll.CreatedAt DESC", 
            nativeQuery = true)
    List<Object[]> getUserLikeListDetails(@Param("userId") String userId);

    /**
     * 查詢用戶喜好清單摘要
     * 
     * @param userId 使用者ID
     * @return 摘要資訊
     */
    @Query(value = "SELECT " +
            "u.UserName, u.Email, " +
            "COUNT(ll.SN) as TotalProducts, " +
            "SUM(ll.TotalAmount) as GrandTotalAmount, " +
            "SUM(ll.TotalFee) as GrandTotalFee, " +
            "GROUP_CONCAT(p.ProductName ORDER BY ll.CreatedAt SEPARATOR ', ') as ProductNames " +
            "FROM LikeList ll " +
            "INNER JOIN User u ON ll.UserID = u.UserID " +
            "INNER JOIN Product p ON ll.ProductNo = p.No " +
            "WHERE ll.UserID = :userId " +
            "GROUP BY u.UserID, u.UserName, u.Email", 
            nativeQuery = true)
    List<Object[]> getUserLikeListSummary(@Param("userId") String userId);

    /**
     * 根據使用者ID查詢喜好清單，按建立時間降序排列
     */
    @Query("SELECT ll FROM LikeList ll " +
           "LEFT JOIN FETCH ll.user u " +
           "LEFT JOIN FETCH ll.product p " +
           "WHERE ll.userId = :userId " +
           "ORDER BY ll.createdAt DESC")
    List<LikeList> findByUserUserIDOrderByCreatedAtDesc(@Param("userId") String userId);

    /**
     * 根據使用者ID和產品編號查詢
     */
    @Query("SELECT ll FROM LikeList ll " +
           "LEFT JOIN FETCH ll.user u " +
           "LEFT JOIN FETCH ll.product p " +
           "WHERE ll.userId = :userId AND ll.productNo = :productNo")
    Optional<LikeList> findByUserUserIDAndProductNo(@Param("userId") String userId, 
                                                     @Param("productNo") Integer productNo);

    /**
     * 根據使用者ID查詢喜好清單數量
     */
    @Query("SELECT COUNT(ll) FROM LikeList ll WHERE ll.userId = :userId")
    Long countByUserUserID(@Param("userId") String userId);

    /**
     * 根據產品編號查詢喜好清單
     */
    @Query("SELECT ll FROM LikeList ll " +
           "LEFT JOIN FETCH ll.user u " +
           "LEFT JOIN FETCH ll.product p " +
           "WHERE ll.productNo = :productNo")
    List<LikeList> findByProductNo(@Param("productNo") Integer productNo);
} 