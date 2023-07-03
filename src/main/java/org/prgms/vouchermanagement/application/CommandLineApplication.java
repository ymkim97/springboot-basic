package org.prgms.vouchermanagement.application;

import org.prgms.vouchermanagement.global.constant.ExceptionMessageConstant;
import org.prgms.vouchermanagement.global.io.Console;
import org.prgms.vouchermanagement.voucher.service.VoucherService;
import org.prgms.vouchermanagement.voucher.domain.entity.Voucher;
import org.prgms.vouchermanagement.voucher.VoucherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.UUID;

@Component
public class CommandLineApplication implements CommandLineRunner, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(CommandLineApplication.class);

    private final Console console;
    private final VoucherService voucherService;
    private ApplicationContext applicationContext;

    public CommandLineApplication(Console console, VoucherService voucherService) {
        this.console = console;
        this.voucherService = voucherService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        while (true) {

            console.printCommandMenu();
            try {

                CommandMenu currentCommand = CommandMenu.getCommandMenu(console.getCommand());
                switch (currentCommand) {
                    case EXIT -> { return; }
                    case CREATE_NEW_VOUCHER -> selectNewVoucher();
                    case SHOW_VOUCHER_LIST -> showVoucherList();
                    case SHOW_BLACK_LIST -> showBlackList();
                    default -> throw new IllegalArgumentException(ExceptionMessageConstant.COMMAND_INPUT_EXCEPTION);
                }
            } catch (RuntimeException | IOException e) {
                if (e instanceof NoSuchFileException){
                    logger.error("No csv file Error");
                } else if (!(e instanceof InputMismatchException)) {
                    logger.error("Command Input Error");
                }
                System.out.println(e.getMessage());
            }
        }
    }

    public void selectNewVoucher() {
        long amountOrPercent = 0;

        console.printSelectVoucherType();
        VoucherType voucherType = VoucherType.getVoucherType(console.getVoucherTypeInput());

        if (voucherType == VoucherType.FIXED_AMOUNT_VOUCHER_TYPE) {
            amountOrPercent = console.getFixedVoucherAmount();
        } else if (voucherType == VoucherType.PERCENT_DISCOUNT_VOUCHER_TYPE) {
            amountOrPercent = console.getPercentDiscount();
        }

        voucherService.createNewVoucher(voucherType, amountOrPercent);
        console.printSavedFinished();
    }

    public void showVoucherList() {
        VoucherType voucherListType;

        console.printSelectVoucherListType();
        voucherListType = VoucherType.getVoucherType(console.getVoucherTypeInput());
        Map<UUID, Voucher> voucherList = voucherService.getVoucherList();
        console.printVoucherList(voucherList, voucherListType);
    }

    private void showBlackList() throws IOException {
        Resource resource = applicationContext.getResource("customer_blacklist.csv");
        try {
            console.printCustomerBlackList(resource.getFile().toPath().toString());
        } catch (IOException e){
            throw new NoSuchFileException(ExceptionMessageConstant.NO_BLACK_LIST_FILE_EXCEPTION);
        }
    }

}
