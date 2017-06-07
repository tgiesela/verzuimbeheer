package com.gieselaar.verzuimbeheer.services;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import org.apache.log4j.Logger;
import org.eclipse.persistence.exceptions.OptimisticLockException;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBase;
import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException.__applicationExceptiontype;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class BeanBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PersistenceContext protected transient EntityManager em;
	@Resource private transient EJBContext context;
	protected static final Logger log = Logger.getLogger(BeanBase.class);

	private Integer currentuser = null;
	protected List<?> getResultList(Query q) throws VerzuimApplicationException{
		try{
			return q.getResultList();
		} catch (Exception e){
			throw dbException(e,"getResultList(): " + e.getMessage());
		}
	}
	protected Object getSingleResult(Query q) throws VerzuimApplicationException{
		try{
			return q.getSingleResult();
		} catch (Exception e){
			throw dbException(e,"getSingleResult(): " + e.getMessage());
		}
	}
	protected VerzuimApplicationException dbException(Exception ex, String operation) {
		context.setRollbackOnly();
		return new VerzuimApplicationException(ex, operation + ":" + ex.getMessage());
	}
	protected VerzuimApplicationException dbException(Exception ex, __applicationExceptiontype exctype) {
		context.setRollbackOnly();
		return new VerzuimApplicationException(ex, ex.getMessage(),exctype);
	}
	protected ValidationException applicationException(ValidationException ex) {
		context.setRollbackOnly();
		return ex;
	}

	public <T extends EntityBaseId> void updateEntity(T entity) throws VerzuimApplicationException {
		log.debug("Update " + entity.toString() + ", class: "
				+ entity.getClass().getSimpleName());
		try{
			em.merge(entity);
			em.flush();
		} catch (OptimisticLockException | javax.persistence.OptimisticLockException e){
			throw dbException(e,__applicationExceptiontype.OPTIMISTICLOCKEXCEPTION);
		} catch (Exception e){
			throw dbException(e,"updateEntity(): " + e.getMessage());
		}
	}

	public <T extends EntityBase> void updateEntity(T entity) throws VerzuimApplicationException {
		log.debug("Update " + entity.toString() + ", class: "
				+ entity.getClass().getSimpleName());
		try{
			em.merge(entity);
			em.flush();
		} catch (OptimisticLockException | javax.persistence.OptimisticLockException e){
			throw dbException(e,__applicationExceptiontype.OPTIMISTICLOCKEXCEPTION);
		} catch (Exception e){
			throw dbException(e,"updateEntity(): " + e.getMessage());
		}
	}
	public <T extends EntityBaseId> void insertEntity(T entity) throws VerzuimApplicationException {
		log.debug("Insert " + entity.toString());
		try{
			em.persist(entity);
			em.flush();
		} catch (OptimisticLockException | javax.persistence.OptimisticLockException e){
			throw dbException(e,__applicationExceptiontype.OPTIMISTICLOCKEXCEPTION);
		} catch (Exception e){
			throw dbException(e,"insertEntity(): " + e.getMessage());
		}
	}
	public <T extends EntityBase> void insertEntity(T entity) throws VerzuimApplicationException {
		log.debug("Insert " + entity.toString());
		try{
			em.persist(entity);
			em.flush();
		} catch (OptimisticLockException | javax.persistence.OptimisticLockException e){
			throw dbException(e,__applicationExceptiontype.OPTIMISTICLOCKEXCEPTION);
		} catch (Exception e){
			throw dbException(e,"insertEntity(): " + e.getMessage());
		}
	}

	public <T extends EntityBase> void deleteEntity(T entity) throws VerzuimApplicationException {
		log.debug("Delete " + entity.toString());
		try{
			entity = em.merge(entity);
			em.remove(entity);
		} catch (OptimisticLockException | javax.persistence.OptimisticLockException e){
			throw dbException(e,__applicationExceptiontype.OPTIMISTICLOCKEXCEPTION);
		} catch (Exception e){
			throw dbException(e,"deleteEntity(): " + e.getMessage());
		}
	}
	public int executeUpdate(Query q) throws VerzuimApplicationException{
		log.debug("ExecuteUpdate " + q.toString());
		try{
			return q.executeUpdate();
		} catch (OptimisticLockException | javax.persistence.OptimisticLockException e){
			throw dbException(e,__applicationExceptiontype.OPTIMISTICLOCKEXCEPTION);
		} catch (Exception e){
			throw dbException(e,"ExecuteUpdate(): " + e.getMessage());
		}
	}
	public Query createQuery(String q) throws VerzuimApplicationException{
		log.debug("createQuery " + q);
		try{
			return em.createQuery(q);
		} catch (OptimisticLockException | javax.persistence.OptimisticLockException e){
			throw dbException(e,__applicationExceptiontype.OPTIMISTICLOCKEXCEPTION);
		} catch (Exception e){
			throw dbException(e,"createQuery(): " + e.getMessage());
		}
	}
	public Integer getCurrentuser() {
		return currentuser;
	}
	public void setCurrentuser(Integer currentuser) {
		this.currentuser = currentuser;
	}
	@PrePassivate
	public void passivate(){
		log.info("BeanBase beeing passivated: " + this.getClass().getName() + " " + this.getClass().hashCode());
	}
	@PostActivate
	public void activate(){
		log.info("BeanBase beeing activated: " + this.getClass().getName() + " " + this.getClass().hashCode());
	}
	@PreDestroy
	public void destroyinfo(){
		log.info("BeanBase beeing destroyed: " + this.getClass().getName() + " " + this.getClass().hashCode());
	}
	@PostConstruct
	public void constructinfo(){
		log.info("BeanBase created: " + this.getClass().getName() + " " + this.getClass().hashCode());
	}
}
