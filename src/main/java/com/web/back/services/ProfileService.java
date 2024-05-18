package com.web.back.services;

import com.web.back.model.entities.Profile;
import com.web.back.model.entities.ProfilePermission;
import com.web.back.model.entities.ProfilePermissionId;
import com.web.back.model.requests.ProfileRequest;
import com.web.back.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserProfileRepository userProfileRepository;
    private final PermissionRepository permissionRepository;
    private final ProfilePermissionRepository profilePermissionRepository;

    public ProfileService(ProfileRepository profileRepository,
                          UserProfileRepository userProfileRepository,
                          PermissionRepository permissionRepository,
                          ProfilePermissionRepository profilePermissionRepository) {
        this.profileRepository = profileRepository;
        this.userProfileRepository = userProfileRepository;
        this.permissionRepository = permissionRepository;
        this.profilePermissionRepository = profilePermissionRepository;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Profile save(ProfileRequest request) {
        Optional<Profile> perfilOptional = profileRepository.findByDescription(request.description());

        if (perfilOptional.isPresent()) {
            throw new RuntimeException("Perfil ya existe");
        }

        Profile profile = new Profile();
        profile.setDescription(request.description());

        profileRepository.save(profile);

        saveProfilePermissions(profile, request.permissionKeys());

        return profile;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Profile update(ProfileRequest request) {
        Optional<Profile> profileOptional = profileRepository.findById(request.profileId());

        if (profileOptional.isEmpty()) {
            throw new RuntimeException("Perfil no encontrado");
        }

        Optional<Profile> duplicatedProfile = profileRepository.findByDescriptionAndIdIsNot(request.description(), request.profileId());
        if (duplicatedProfile.isPresent()) {
            throw new RuntimeException("Otro perfil ya tiene ese nombre");
        }

        profileOptional.get().setDescription(request.description());

        profileRepository.save(profileOptional.get());

        profilePermissionRepository.deleteByProfile(profileOptional.get());

        saveProfilePermissions(profileOptional.get(), request.permissionKeys());

        return profileOptional.get();
    }

    @Transactional(rollbackFor = {Exception.class})
    public void delete(Integer id) {
        Optional<Profile> OptionalProfile = profileRepository.findById(id);

        if (OptionalProfile.isEmpty()) {
            throw new RuntimeException("Perfil no encontrado");
        }

        if (userProfileRepository.findByProfile(OptionalProfile.get()).isPresent()) {
            throw new RuntimeException("No se puede borrar un perfil con usuarios asociados");
        }

        profileRepository.deleteById(id);
    }

    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    private void saveProfilePermissions(Profile profile, List<String> permissions) {
        permissions.forEach(permissionKey -> {
            var permission = permissionRepository.findByKeyName(permissionKey);

            if (permission.isEmpty()) {
                throw new RuntimeException(String.format("Permiso %s no existe", permissionKey));
            }

            ProfilePermission profilePermission = new ProfilePermission();

            ProfilePermissionId profilePermissionId = new ProfilePermissionId();
            profilePermissionId.setProfileId(profile.getId());
            profilePermissionId.setPermissionId(permission.get().getId());

            profilePermission.setId(profilePermissionId);

            profilePermission.setProfile(profile);
            profilePermission.setPermission(permission.get());

            profilePermissionRepository.save(profilePermission);
        });
    }
}
