package com.july.log.service;

public interface IService<T,S> {

    T getById(S id);

}
