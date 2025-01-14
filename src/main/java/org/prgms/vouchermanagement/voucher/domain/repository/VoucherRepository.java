package org.prgms.vouchermanagement.voucher.domain.repository;

import org.prgms.vouchermanagement.voucher.domain.entity.Voucher;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository {

    Optional<Voucher> findById(UUID voucherId);
    Voucher saveVoucher(Voucher voucher);
    Map<UUID, Voucher> getVoucherList();
}
