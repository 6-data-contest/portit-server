package org.ssafy.datacontest.service;

import org.ssafy.datacontest.dto.SliceResponseDto;
import org.ssafy.datacontest.dto.company.*;

public interface CompanyService {
    void fetchAndSaveCompanies();
    ArticleLikeResponse toggleLike(Long articleId, String companyName);
    SliceResponseDto<CompanyScrollResponse> getCompaniesByCursor(CompanyScrollRequest companyScrollRequest);
    CompanyResponse getCompany(String companyName);
    Long updateCompany(CompanyUpdateRequest companyUpdateRequest, String companyName);
}
