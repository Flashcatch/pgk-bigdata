package repository;

import domain.mobile.Credentials;
import domain.mobile.UserCard;
import mappers.AuthMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import javax.jdo.annotations.Transactional;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AuthRepository {

    private static final Logger LOG = LoggerFactory.getLogger(AuthRepository.class);

    private final AuthMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public AuthRepository(AuthMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Credentials> createUser(Credentials user) {
        LOG.debug(">> createUser");

        return CompletableFuture.supplyAsync(() -> {
            mapper.insertUser(user);
            return user;
        }, ec);

    }

    @Transactional
    public CompletionStage<Credentials> updateUser(Credentials user) {
        LOG.debug(">> updateUser");
        Objects.requireNonNull(user);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateUser(user);
            return user;
        }, ec);
    }

    public void setUserType (String phone, Boolean mr, Boolean lkk) {
        mapper.updateUserType(phone, mr, lkk);
    }
/*
    @Transactional
    public CompletionStage<Location> updateLocation(Location location) {
        LOG.debug(">> updateLocation");
        Objects.requireNonNull(location);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateLocationById(location);
            return location;
        }, ec);
    }

    public CompletionStage<Boolean> deleteLocation(Long id) {
        LOG.debug(">> deleteLocation");
        return CompletableFuture.supplyAsync(() ->
                        deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final Location data = mapper.selectLocationById(id);
        if (data != null) {
            mapper.deleteLocation(id);
            result = true;
        }
        return result;
    }*/

    public Credentials getUserByPhone(String phone) {
        LOG.debug(">> getUserByPhone");
        return mapper.selectUserByPhone(phone);
    }

    public UserCard getUserCardByPhone(String phone) {
        LOG.debug(">> getUserCardByPhone");
        return mapper.selectUserCardByPhone(phone);
    }
}
