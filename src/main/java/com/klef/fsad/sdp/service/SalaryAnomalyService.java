package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Salary;
import com.klef.fsad.sdp.model.SalaryAnomaly;
import com.klef.fsad.sdp.repository.SalaryAnomalyRepository;
import com.klef.fsad.sdp.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SalaryAnomalyService {

    @Autowired
    private SalaryAnomalyRepository anomalyRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    // Anomaly detection threshold (20% as per SRS requirement)
    private static final double ANOMALY_THRESHOLD = 20.0;

    // Detect anomalies for a salary record
    public List<SalaryAnomaly> detectAnomalies(Salary currentSalary) {
        Employee employee = currentSalary.getEmployee();
        List<SalaryAnomaly> detectedAnomalies = new java.util.ArrayList<>();

        // Get previous salary records
        List<Salary> previousSalaries = salaryRepository.findPreviousSalaries(
                employee,
                currentSalary.getYear(),
                currentSalary.getMonth()
        );

        if (!previousSalaries.isEmpty()) {
            Salary previousSalary = previousSalaries.get(0);

            // Check for sudden salary increase/decrease (±20% threshold)
            double currentNet = currentSalary.getNetSalary();
            double previousNet = previousSalary.getNetSalary();

            if (previousNet > 0) {
                double deviationPercentage = ((currentNet - previousNet) / previousNet) * 100;
                double absDeviation = Math.abs(deviationPercentage);

                if (absDeviation >= ANOMALY_THRESHOLD) {
                    String anomalyType = deviationPercentage > 0 ? "SUDDEN_INCREASE" : "SUDDEN_DECREASE";
                    String description = String.format(
                            "Salary %s by %.2f%% compared to previous month (Previous: %.2f, Current: %.2f)",
                            anomalyType.equals("SUDDEN_INCREASE") ? "increased" : "decreased",
                            absDeviation,
                            previousNet,
                            currentNet
                    );

                    SalaryAnomaly anomaly = new SalaryAnomaly(
                            currentSalary,
                            employee,
                            anomalyType,
                            currentNet,
                            previousNet
                    );
                    anomaly.setDescription(description);

                    detectedAnomalies.add(anomalyRepository.save(anomaly));
                }
            }
        }

        // Check for unusual deductions
        double totalDeductions = currentSalary.getEpfDeduction() +
                currentSalary.getEtfDeduction() +
                currentSalary.getOtherDeductions();
        double deductionPercentage = (totalDeductions / currentSalary.getGrossSalary()) * 100;

        if (deductionPercentage > 30) { // If deductions exceed 30% of gross
            String description = String.format(
                    "Unusually high deductions detected: %.2f%% of gross salary (Total deductions: %.2f)",
                    deductionPercentage,
                    totalDeductions
            );

            SalaryAnomaly anomaly = new SalaryAnomaly();
            anomaly.setSalary(currentSalary);
            anomaly.setEmployee(employee);
            anomaly.setAnomalyType("UNUSUAL_DEDUCTION");
            anomaly.setCurrentAmount(totalDeductions);
            anomaly.setPreviousAmount(0);
            anomaly.setDeviationPercentage(deductionPercentage);
            anomaly.setDescription(description);
            anomaly.setSeverity("HIGH");
            anomaly.setDetectedDate(LocalDate.now());
            anomaly.setStatus("PENDING");

            detectedAnomalies.add(anomalyRepository.save(anomaly));
        }

        // Check for missing attendance data
        if (currentSalary.getTotalDaysWorked() == 0) {
            String description = "No attendance records found for this month";

            SalaryAnomaly anomaly = new SalaryAnomaly();
            anomaly.setSalary(currentSalary);
            anomaly.setEmployee(employee);
            anomaly.setAnomalyType("MISSING_ATTENDANCE");
            anomaly.setCurrentAmount(currentSalary.getNetSalary());
            anomaly.setPreviousAmount(0);
            anomaly.setDescription(description);
            anomaly.setSeverity("CRITICAL");
            anomaly.setDetectedDate(LocalDate.now());
            anomaly.setStatus("PENDING");

            detectedAnomalies.add(anomalyRepository.save(anomaly));
        }

        return detectedAnomalies;
    }

    // Get all anomalies
    public List<SalaryAnomaly> getAllAnomalies() {
        return anomalyRepository.findAll();
    }

    // Get anomalies by status
    public List<SalaryAnomaly> getAnomaliesByStatus(String status) {
        return anomalyRepository.findByStatusOrderByDetectedDateDesc(status);
    }

    // Get anomalies by severity
    public List<SalaryAnomaly> getAnomaliesBySeverity(String severity) {
        return anomalyRepository.findBySeverity(severity);
    }

    // Get anomalies by employee
    public List<SalaryAnomaly> getAnomaliesByEmployee(Long employeeId, Employee employee) {
        return anomalyRepository.findByEmployeeOrderByDetectedDateDesc(employee);
    }

    // Get pending anomalies
    public List<SalaryAnomaly> getPendingAnomalies() {
        return anomalyRepository.findByStatus("PENDING");
    }

    // Review anomaly
    public SalaryAnomaly reviewAnomaly(Long anomalyId, String status, String resolutionNotes, String reviewedBy) {
        Optional<SalaryAnomaly> anomalyOpt = anomalyRepository.findById(anomalyId);
        if (anomalyOpt.isEmpty()) {
            throw new RuntimeException("Anomaly not found");
        }

        SalaryAnomaly anomaly = anomalyOpt.get();
        anomaly.setStatus(status); // REVIEWED, RESOLVED, IGNORED
        anomaly.setResolutionNotes(resolutionNotes);
        anomaly.setReviewedBy(reviewedBy);
        anomaly.setReviewedDate(LocalDate.now());

        return anomalyRepository.save(anomaly);
    }

    // Get anomaly by ID
    public Optional<SalaryAnomaly> getAnomalyById(Long id) {
        return anomalyRepository.findById(id);
    }

    // Delete anomaly
    public void deleteAnomaly(Long id) {
        anomalyRepository.deleteById(id);
    }

    // Get critical anomalies
    public List<SalaryAnomaly> getCriticalAnomalies() {
        return anomalyRepository.findByStatusAndSeverityOrderByDetectedDateDesc("PENDING", "CRITICAL");
    }
}