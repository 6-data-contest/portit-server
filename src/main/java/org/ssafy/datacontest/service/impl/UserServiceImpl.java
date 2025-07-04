package org.ssafy.datacontest.service.impl;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.ssafy.datacontest.dto.company.CompanySummaryResponse;
import org.ssafy.datacontest.dto.company.LikedArticleResponse;
import org.ssafy.datacontest.dto.user.UserAlertResponse;
import org.ssafy.datacontest.dto.user.UserResponse;
import org.ssafy.datacontest.dto.user.UserUpdateRequest;
import org.ssafy.datacontest.entity.*;
import org.ssafy.datacontest.enums.ErrorCode;
import org.ssafy.datacontest.exception.CustomException;
import org.ssafy.datacontest.mapper.ArticleLikeMapper;
import org.ssafy.datacontest.mapper.CompanyMapper;
import org.ssafy.datacontest.mapper.UserMapper;
import org.ssafy.datacontest.repository.*;
import org.ssafy.datacontest.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final CompanyRepository companyRepository;
    private final ChatRoomRepository chatRoomRepository;

    public UserServiceImpl(UserRepository userRepository, ArticleRepository articleRepository, ArticleLikeRepository articleLikeRepository, CompanyRepository companyRepository, ChatRoomRepository chatRoomRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.articleLikeRepository = articleLikeRepository;
        this.companyRepository = companyRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public UserResponse getUser(String username) {
        User user = userRepository.findByLoginId(username);

        if(user == null){
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);
        }

        List<Article> userArticles = articleRepository.findByUser_Id(user.getId());

        List<LikedArticleResponse> likedArticleResponses = new ArrayList<>();
        if (!userArticles.isEmpty()) {
            likedArticleResponses = userArticles.stream()
                    .map(ArticleLikeMapper::toLikedArticleResponse)
                    .toList();
        }

        return UserMapper.toResponse(user, likedArticleResponses);
    }

    @Override
    @Transactional
    public Long updateUser(UserUpdateRequest userUpdateRequest, String userName) {
        User user = userRepository.findByLoginId(userName);

        if(user == null){
            throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);
        }

        user.updateUser(userUpdateRequest.getUserNickname());
        return user.getId();
    }

    @Override
    public List<UserAlertResponse> getUserAlerts(String userName) {
        User user = userRepository.findByLoginId(userName);
        if(user == null) throw new CustomException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED_USER);

        List<Like> likes = articleLikeRepository.findByUser_Id(user.getId());
        if(likes.isEmpty()) return new ArrayList<>();

        List<UserAlertResponse> userAlertResponses = new ArrayList<>();
        for (Like like : likes) {
            Company company = companyRepository.findByCompanyId(like.getCompany().getCompanyId());
            Article article = articleRepository.findDeletedArticleById(like.getArticle().getArtId());
            userAlertResponses.add(UserMapper.toAlertResponse(company, like, article));
        }

        return userAlertResponses;
    }

    @Override
    @Transactional
    public CompanySummaryResponse checkAlert(Long alertId, String userName) {
        Like like = articleLikeRepository.findByLikeId(alertId);
        like.setReaded(true);

        Company company = companyRepository.findByCompanyId(like.getCompany().getCompanyId());
        ChatRoom chatRoom = chatRoomRepository.findByArticle_ArtIdAndCompany_CompanyId(like.getArticle().getArtId(), company.getCompanyId());

        boolean chatting = false;
        if(chatRoom != null) chatting = true;
        return CompanyMapper.toCompanySummaryResponse(company, chatting);
    }
}
