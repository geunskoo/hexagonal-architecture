package com.geunskoo.hexagonalarchitecture.application.port.in;

public interface SendMoneyUseCase {

    boolean sendMoney(SendMoneyCommand command);

}