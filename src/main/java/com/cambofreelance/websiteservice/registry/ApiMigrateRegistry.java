//package com.cambofreelance.lotteryserver.registry;
//
//import com.cambofreelance.lotteryserver.caches.ResponseCodeRedisCache;
//import com.cambofreelance.lotteryserver.caches.ResponseManagerCache;
//import com.cambofreelance.lotteryserver.dto.ResponseCodeDto;
//import com.cambofreelance.lotteryserver.dto.keycloak.KeycloakRoleResponse;
//import com.cambofreelance.lotteryserver.dto.keycloak.user.KeycloakUserResponse;
//import com.cambofreelance.lotteryserver.dto.request.CredentialsItem;
//import com.cambofreelance.lotteryserver.dto.request.KeycloakRoleRequest;
//import com.cambofreelance.lotteryserver.dto.request.KeycloakUserCreateRequest;
//import com.cambofreelance.lotteryserver.entities.ResponseCodeEntity;
//import com.cambofreelance.lotteryserver.entities.RoleEntity;
//import com.cambofreelance.lotteryserver.entities.UserEntity;
//import com.cambofreelance.lotteryserver.logger.contants.Constants;
//import com.cambofreelance.lotteryserver.repository.ResponseCodeRepository;
//import com.cambofreelance.lotteryserver.repository.RoleRepository;
//import com.cambofreelance.lotteryserver.repository.UserRepository;
//import com.cambofreelance.lotteryserver.service.KeycloakUserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ApiMigrateRegistry {
//
//    private final ResponseCodeRepository responseCodeRepository;
//    private final ResponseCodeRedisCache responseCodeRedisCache;
//    private final KeycloakUserService keycloakUserService;
//    private final RoleRepository roleRepository;
//    private final UserRepository userRepository;
//
//
//    public void loadComponentInit() {
//        log.info("Loading component ...");
//        this.loadResponseCode();
//        this.seedAdminUser();
//    }
//
//    public void loadResponseCode() {
//        try {
//            Thread threadLoadResponseCode = new Thread(() -> {
//                try {
//                    List<ResponseCodeEntity> responseCodeList = responseCodeRepository.findByStatus(
//                        Constants.STATUS_ACT);
//                    List<ResponseCodeDto> responseCodeDtoList = responseCodeList.stream()
//                        .map(entity -> new ResponseCodeDto(
//                            entity.getId(),
//                            entity.getCode(),
//                            entity.getHttpStatus(),
//                            entity.getKey(),
//                            entity.getType(),
//                            entity.getDescription(),
//                            entity.getMessageEn(),
//                            entity.getMessageKm(),
//                            entity.getMessageCn(),
//                            entity.getStatus()
//                        ))
//                        .toList();
//                    ResponseManagerCache.initRespCodeCache(responseCodeDtoList);
//                    responseCodeRedisCache.initRespCodeCache(responseCodeDtoList);
//                } catch (Exception e) {
//                    log.error("Error loading API routes: {}", e.getMessage(), e);
//                }
//            });
//            threadLoadResponseCode.start();
//        }catch (Exception e){
//            log.error("Init response code error: {}", e.getMessage(), e);
//        }
//    }
//
//    public void seedAdminUser() {
//        try {
//            List<KeycloakRoleResponse> listRole = keycloakUserService.getRoles().block();
//            log.info("List Role: {}", listRole);
//            int index = 1;
//            if (Objects.nonNull(listRole)) {
//                for (KeycloakRoleResponse data : listRole) {
//                    if (data.getDescription().matches("\\$\\{.*\\}")) {
//                      continue;
//                    }
//                    RoleEntity role = roleRepository.findById(data.getId()).orElse(null);
//                    if (role == null) {
//                        RoleEntity newRole = new RoleEntity();
//                        newRole.setId(data.getId());
//                        newRole.setCode(data.getName());
//                        newRole.setName(data.getDescription());
//                        newRole.setDescription(data.getDescription());
//                        newRole.setStatus(Constants.STATUS_ACT);
//                        newRole.setLevel(index++);
//                        roleRepository.save(newRole);
//                    } else {
//                        role.setCode(data.getName());
//                        role.setName(data.getDescription());
//                        role.setDescription(data.getDescription());
//                        role.setStatus(Constants.STATUS_ACT);
//                        role.setLevel(index++);
//                        roleRepository.save(role);
//                    }
//                }
//            }
//
//            var checkUserSuperAdmin = userRepository.findByUsername(Constants.USER_SUPER_ADMIN)
//                .orElse(null);
//            if (Objects.nonNull(checkUserSuperAdmin)) {
//                log.info("Super admin user already exists.");
//                return;
//            }
//
//            var createUser = getUserEntity();
//            // Check user keycloak exist
//            var userExistOnKeycloak = keycloakUserService.getUserByUsername(
//                    createUser.getUsername())
//                .block();
//            if (null != userExistOnKeycloak && !userExistOnKeycloak.isEmpty()) {
//                var userInDb = userRepository.findByUsername(createUser.getUsername());
//                if (userInDb.isPresent()) {
//                    log.info("Super admin user already exists in db");
//                    return;
//                }
//                var role = Objects.requireNonNull(listRole).stream()
//                    .map(r -> {
//                        KeycloakRoleRequest roleRequest = new KeycloakRoleRequest();
//                        roleRequest.setId(r.getId());
//                        roleRequest.setName(r.getName());
//                        roleRequest.setDescription(r.getDescription());
//                        return roleRequest;
//                    })
//                    .toList();
//                userAssignRole(createUser, userExistOnKeycloak.getFirst(), role);
//                return;
//            }
//            // Create user keycloak
//            var keycloakCreateUserRequest = getKeycloakUserCreateRequest(createUser);
//            var createUserKeycloakSuccess = keycloakUserService.createUser(
//                    keycloakCreateUserRequest)
//                .block();
//            log.info("Create super admin user in Keycloak response: {}", createUserKeycloakSuccess);
//            if (Objects.nonNull(createUserKeycloakSuccess)) {
//                var usernameList = keycloakUserService.getUserByUsername(createUser.getUsername())
//                    .block();
//                if (null != usernameList) {
//                    assert listRole != null;
//                    List<KeycloakRoleRequest> role = listRole.stream()
//                        .map(r -> {
//                            KeycloakRoleRequest roleRequest = new KeycloakRoleRequest();
//                            roleRequest.setId(r.getId());
//                            roleRequest.setName(r.getName());
//                            roleRequest.setDescription(r.getDescription());
//                            return roleRequest;
//                        })
//                        .toList();
//                    userAssignRole(createUser, usernameList.getFirst(), role);
//                } else {
//                    log.error(
//                        "Failed to retrieve user ID for super admin after creation in Keycloak.");
//                }
//            }
//        }catch (Exception e) {
//            log.error("Init admin user error: {}", e.getMessage(), e);
//        }
//    }
//
//    private static UserEntity getUserEntity() {
//        var createUser = new UserEntity();
//        createUser.setUsername("super.admin");
//        createUser.setFirstName("System");
//        createUser.setLastName("Administrator");
//        createUser.setEmail("super.admin@gmail.com");
//        createUser.setPhoneNumber("0962505045");
//        createUser.setStatus(Constants.STATUS_ACT);
//        createUser.setCreatedBy(Constants.SYSTEM);
//        createUser.setUpdatedBy(Constants.SYSTEM);
//        createUser.setIsForceChangePassword(Constants.IF_FORCE_CHANGE_PASSWORD_FALSE);
//        createUser.setGender(Constants.GENDER_MALE);
//        createUser.setPassword("Admin@123");
//        createUser.setKcUsername("super.admin");
//        return createUser;
//    }
//
//    private void userAssignRole(UserEntity createUser, KeycloakUserResponse userExistOnKeycloak, List<KeycloakRoleRequest> role) {
//        var userAssignId = userExistOnKeycloak.id();
//        keycloakUserService.assignRoleUser(role, userAssignId).block();
//        var roles = new HashSet<>(roleRepository.findAll());
//        createUser.setRoles(roles);
//        createUser.setId(userAssignId);
//        createUser.setPassword(null);
//        userRepository.save(createUser);
//        log.info("Super admin user created successfully.");
//    }
//
//    private static KeycloakUserCreateRequest getKeycloakUserCreateRequest(UserEntity createUser) {
//        var keycloakCreateUserRequest = new KeycloakUserCreateRequest();
//        List<CredentialsItem> credentials = new ArrayList<>();
//        CredentialsItem credentialsItem = new CredentialsItem();
//        credentialsItem.setTemporary(false);
//        credentialsItem.setValue(createUser.getPassword());
//        credentialsItem.setTemporary(false);
//        credentialsItem.setType("password");
//        credentials.add(credentialsItem);
//        keycloakCreateUserRequest.setCredentials(credentials);
//        keycloakCreateUserRequest.setEnabled(true);
//        keycloakCreateUserRequest.setEmail(createUser.getEmail());
//        keycloakCreateUserRequest.setFirstName(createUser.getFirstName());
//        keycloakCreateUserRequest.setLastName(createUser.getLastName());
//        keycloakCreateUserRequest.setUsername(createUser.getUsername());
//        keycloakCreateUserRequest.setEmailVerified(false);
//        return keycloakCreateUserRequest;
//    }
//
//}