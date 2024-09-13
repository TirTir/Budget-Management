package backend.budget.auth.service;

import backend.budget.auth.dto.RegisterRequest;
import backend.budget.auth.entity.User;
import backend.budget.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void register(RegisterRequest request) throws IllegalAccessException {

        // 계정 중복 확인
        if(userRepository.existsByUserName(request.getUserName())){
            throw new IllegalAccessException("이미 존재하는 계정입니다.");
        }

        User new_user =User.builder()
                .username(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(new_user);
    }
}
