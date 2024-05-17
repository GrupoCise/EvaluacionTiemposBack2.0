package com.web.back.services;

import com.web.back.mappers.ImpersonateResponseMapper;
import com.web.back.model.entities.Impersonation;
import com.web.back.model.entities.User;
import com.web.back.model.entities.UserProfileId;
import com.web.back.model.requests.ImpersonateRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.ImpersonateResponse;
import com.web.back.repositories.ImpersonationRepository;
import com.web.back.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ImpersonateService {
    private final ImpersonationRepository impersonationRepository;
    private final UserRepository userRepository;

    public ImpersonateService(ImpersonationRepository impersonationRepository, UserRepository userRepository) {
        this.impersonationRepository = impersonationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackFor = {Exception.class})
    public CustomResponse<ImpersonateResponse> saveImpersonation(ImpersonateRequest request){
        Optional<User> actor = userRepository.findByUsername(request.userName());
        Optional<User> targetUser = userRepository.findByUsername(request.targetUserName());

        if(actor.isEmpty() || targetUser.isEmpty()){
            return new CustomResponse<ImpersonateResponse>().badRequest("No se encontró el actor o el usuario al que actua como");
        }

        if(actor.get().getUsername().equals(targetUser.get().getUsername())){
            return new CustomResponse<ImpersonateResponse>().badRequest( "No se puede actuar como el mismo usuario");
        }

        if(actor.get().getProfiles().stream().anyMatch(profile -> targetUser.get().getProfiles().contains(profile))){
            return new CustomResponse<ImpersonateResponse>().badRequest( "No se puede actuar como alguien con el mismo perfil");
        }

        Optional<Impersonation> actuaComoOptional = impersonationRepository.findByUserAndTargetUser(actor.get(), targetUser.get());
        if(actuaComoOptional.isPresent()){
            return new CustomResponse<ImpersonateResponse>().badRequest( "Ya existe una relación entre los usuarios");
        }else {
            Impersonation impersonation = new Impersonation();
            impersonation.setUser(actor.get());
            impersonation.setTargetUser(targetUser.get());

            impersonationRepository.save(impersonation);

            return new CustomResponse<ImpersonateResponse>().ok(ImpersonateResponseMapper.mapFrom(impersonation));
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public CustomResponse<Boolean> deleteImpersonation(Integer id){
        Optional<Impersonation> impersonation = impersonationRepository.findById(id);

        if(impersonation.isEmpty()){
            return new CustomResponse<Boolean>().badRequest(false, "No existe una relación entre los usuarios");
        }else {
            impersonationRepository.delete(impersonation.get());
            return new CustomResponse<Boolean>().ok( true, "Actua Como eliminado correctamente");
        }
    }

    public CustomResponse<List<ImpersonateResponse>> getImpersonations(){
        return new CustomResponse<List<ImpersonateResponse>>().ok(
                impersonationRepository.findAll().stream().map(ImpersonateResponseMapper::mapFrom).toList()
        );
    }

    public CustomResponse<List<ImpersonateResponse>> getByUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(value -> new CustomResponse<List<ImpersonateResponse>>().ok(
                    impersonationRepository.findByUser(value).stream().map(ImpersonateResponseMapper::mapFrom).toList())
                )
                .orElseGet(() -> new CustomResponse<List<ImpersonateResponse>>().badRequest( "No se encontró el usuario"));

    }
}
