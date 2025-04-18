package secretstuffs.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import secretstuffs.application.helpers.AuthHelper;
import secretstuffs.domain.dtos.commands.user.ChangePasswordCommand;
import secretstuffs.domain.dtos.commands.user.UpdateUserDetailsCommand;
import secretstuffs.domain.entities.User;
import secretstuffs.domain.models.responses.user.UpdateUserResponseDTO;
import secretstuffs.domain.models.responses.user.FetchUserProfileResponseDTO;
import secretstuffs.domain.dtos.exception.UserException;
import secretstuffs.infrastructure.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthHelper authHelper;

    public UserService(UserRepository userRepository, AuthHelper authHelper) {
        this.userRepository = userRepository;
        this.authHelper = authHelper;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> UserException.userNotFound(email));
    }

    @Transactional
    public void activateUser(String email) {
        User user = findUserByEmail(email);
        if (!user.isActive()) {
            user.setActive(true);
            userRepository.save(user);
        } else {
            throw UserException.userAlreadyActive();
        }
    }

    public boolean isUserRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUserActive(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get().isActive();
        } else {
            throw UserException.userNotFound(email);
        }
    }

    @Transactional
    public UpdateUserResponseDTO updateUserByEmail(UpdateUserDetailsCommand command) {
        User user = findUserByEmail(command.getEmail());
        user.setProfileImageURL(command.getProfileImageURL());
        user.setFirstName(command.getFirstName());
        user.setLastName(command.getLastName());
        return buildUpdateUserResponse(userRepository.save(user));
    }

    public FetchUserProfileResponseDTO getUserProfileByEmail(String email) {
        return buildUserProfileResponse(findUserByEmail(email));
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        User user = findUserByEmail(email);
        userRepository.delete(user);
    }

    public List<FetchUserProfileResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::buildUserProfileResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        String email = command.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserException.userNotFound(email));

        if (!authHelper.passwordMatches(command.getOldPassword(), user.getPassword())) {
            throw UserException.invalidOldPassword();
        }

        if (!command.getNewPassword().equals(command.getConfirmPassword())) {
            throw UserException.passwordsDoNotMatch();
        }
        user.setPassword(authHelper.encryptPassword(command.getNewPassword()));
        userRepository.save(user);
    }

    private UpdateUserResponseDTO buildUpdateUserResponse(User user) {
         UpdateUserResponseDTO.UpdateUserResponseDTOBuilder updateUserResponseDTOBuilder = UpdateUserResponseDTO.builder();
        updateUserResponseDTOBuilder.email(user.getEmail());
        updateUserResponseDTOBuilder.firstName(user.getFirstName());
        updateUserResponseDTOBuilder.lastName(user.getLastName());
        updateUserResponseDTOBuilder.profileImageURL(user.getProfileImageURL());

        return updateUserResponseDTOBuilder.build();
    }

    private FetchUserProfileResponseDTO buildUserProfileResponse(User user) {
        FetchUserProfileResponseDTO.FetchUserProfileResponseDTOBuilder builder = FetchUserProfileResponseDTO.builder();
        builder.id(user.getId());
        builder.email(user.getEmail());
        builder.firstName(user.getFirstName());
        builder.lastName(user.getLastName());
        builder.profileImageURL(user.getProfileImageURL());
        builder.active(user.isActive());

        return builder.build();
    }

    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw UserException.userNotFound(userId);
        }
        return user.get();
    }
}
