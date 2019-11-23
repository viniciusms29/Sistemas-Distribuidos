package com.marketplace.utils;

import java.io.Serializable;
import java.util.List;

public interface InterfaceService<Objeto, I extends Serializable> {

	public void save(Objeto objeto) throws ServiceException;

	public void update(Objeto obj) throws ServiceException;

	public void delete(I id) throws ServiceException;

	public List<Objeto> findAll();

	public Objeto findID(I id) throws ServiceException;

}
