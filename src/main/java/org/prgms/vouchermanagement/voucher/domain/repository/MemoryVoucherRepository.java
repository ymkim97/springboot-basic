package org.prgms.vouchermanagement.voucher.domain.repository;

import org.prgms.vouchermanagement.voucher.domain.entity.Voucher;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryVoucherRepository implements VoucherRepository{
    private final Map<UUID, Voucher> voucherStorage = new ConcurrentHashMap<>();

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.ofNullable(voucherStorage.get(voucherId));
    }

    @Override
    public void saveVoucher(UUID voucherId, Voucher voucher) {
        voucherStorage.put(voucherId, voucher);
    }

    @Override
    public Map<UUID, Voucher> getVoucherList() {
        if (!voucherStorage.isEmpty()) {
            return Collections.unmodifiableMap(voucherStorage);
        }
        return Collections.emptyMap();
    }
}
