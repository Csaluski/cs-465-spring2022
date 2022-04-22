/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package appserver.job.impl;

import appserver.job.Tool;

/**
 *
 * @author k.sato
 */
public class Fibonacci implements Tool{
    FibonacciAux helper = null;
    
    @Override
    public Object go(Object parameters) {
        
        helper = new FibonacciAux((Long) parameters);
        return helper.getResult();
    }
    
}
