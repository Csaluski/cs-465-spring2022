/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package appserver.job.impl;

/**
 *
 * @author k.sato
 */
public class FibonacciAux {
    Long number = null;
    
    public FibonacciAux(Long number) {
        this.number = number;
    }
    
    public Long getResult() {
        return fibonacci(number);
    }
    
    private Long fibonacci(Long num){
        if(num == 1){
            return 1L;
        }else if(num == 0){
            return 0L;
        }
        return fibonacci(num - 1) + fibonacci(num - 2);
    }
    
}
