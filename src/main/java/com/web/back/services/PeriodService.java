package com.web.back.services;

import com.web.back.model.dto.PeriodDto;
import com.web.back.model.entities.Period;
import com.web.back.model.requests.PeriodRequest;
import com.web.back.repositories.PeriodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PeriodService {
    private final PeriodRepository periodRepository;

    public PeriodService(PeriodRepository periodRepository) {
        this.periodRepository = periodRepository;
    }

    public PeriodDto createPeriod(PeriodRequest request) {
        Period period = fromRequest(request);
        Period saved = periodRepository.save(period);
        return toDto(saved);
    }

    public PeriodDto getPeriodById(Long id) {
        return periodRepository.findById(id)
            .map(this::toDto)
            .orElse(null);
    }

    public List<PeriodDto> getAllPeriods() {
        return periodRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public PeriodDto updatePeriod(Long id, PeriodRequest request) {
        Optional<Period> optional = periodRepository.findById(id);
        if (optional.isEmpty()) throw new RuntimeException("Period not found");
        Period period = fromRequest(id, request);
        Period saved = periodRepository.save(period);
        return toDto(saved);
    }

    public void deletePeriod(Long id) {
        Optional<Period> optional = periodRepository.findById(id);
        if (optional.isEmpty()) throw new RuntimeException("Period not found");

        periodRepository.deleteById(id);
    }

    private Period fromRequest(Long id, PeriodRequest request) {
        Period period = fromRequest(request);
        period.setId(id);

        return period;
    }

    private Period fromRequest(PeriodRequest request) {
        Period period = new Period();
        period.setGrouper1(request.grouper1());
        period.setGrouper2(request.grouper2());
        period.setGrouper3(request.grouper3());
        period.setGrouper4(request.grouper4());
        period.setGrouper5(request.grouper5());
        period.setFromDate(request.fromDate());
        period.setToDate(request.toDate());
        return period;
    }

    private PeriodDto toDto(Period period) {
        return new PeriodDto(
            period.getId(),
            period.getGrouper1(),
            period.getGrouper2(),
            period.getGrouper3(),
            period.getGrouper4(),
            period.getGrouper5(),
            period.getFromDate(),
            period.getToDate()
        );
    }
}
