package com.example;

import java.util.Date;

public class HelloWorld {
    public static void main(String[] args) {
        // Vulnerabilidade: uso de Date mutável
        Date now = new Date();
        System.out.println("Data atual: " + now);

        // Vulnerabilidade: senha hardcoded
        String senha = "123456";
        System.out.println("Senha: " + senha);

        // Vulnerabilidade: comparação de String com ==
        String a = "teste";
        String b = new String("teste");
        if(a == b) {
            System.out.println("Strings iguais!");
        } else {
            System.out.println("Strings diferentes!");
        }

        // Vulnerabilidade: uso de System.exit
        System.exit(0);
    }
}
