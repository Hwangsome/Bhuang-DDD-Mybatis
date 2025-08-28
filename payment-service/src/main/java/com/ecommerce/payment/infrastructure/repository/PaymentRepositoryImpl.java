package com.ecommerce.payment.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.repository.PaymentRepository;
import com.ecommerce.payment.domain.valueobject.PaymentId;
import com.ecommerce.payment.infrastructure.entity.PaymentPO;
import com.ecommerce.payment.infrastructure.mapper.PaymentDataMapper;
import com.ecommerce.payment.infrastructure.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired
    private PaymentDataMapper paymentDataMapper;

    @Override
    public Payment save(Payment payment) {
        PaymentPO paymentPO = paymentDataMapper.paymentToPaymentPO(payment);
        if (paymentPO.getId() == null) {
            paymentMapper.insert(paymentPO);
        } else {
            paymentMapper.updateById(paymentPO);
        }
        return paymentDataMapper.paymentPOToPayment(paymentPO);
    }

    @Override
    public Optional<Payment> findById(PaymentId paymentId) {
        LambdaQueryWrapper<PaymentPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentPO::getPaymentId, paymentId.getValue());
        PaymentPO paymentPO = paymentMapper.selectOne(queryWrapper);
        return paymentPO != null ? 
            Optional.of(paymentDataMapper.paymentPOToPayment(paymentPO)) : 
            Optional.empty();
    }

    @Override
    public List<Payment> findByOrderId(String orderId) {
        List<PaymentPO> paymentPOs = paymentMapper.findByOrderId(orderId);
        return paymentPOs.stream()
            .map(paymentDataMapper::paymentPOToPayment)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByUserId(String userId) {
        List<PaymentPO> paymentPOs = paymentMapper.findByUserId(userId);
        return paymentPOs.stream()
            .map(paymentDataMapper::paymentPOToPayment)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(PaymentId paymentId) {
        LambdaQueryWrapper<PaymentPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentPO::getPaymentId, paymentId.getValue());
        paymentMapper.delete(queryWrapper);
    }
}