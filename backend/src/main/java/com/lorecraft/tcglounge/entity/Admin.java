package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

/**
 * 관리자 엔티티 (User 하위 클래스)
 *
 * 역할:
 * - 시스템 관리자의 추가 정보 관리
 * - 관리자 권한 수준 및 부서 정보 보관
 * - 어드민 페이지 접근 권한 확인용
 *
 * 사용 지침:
 * - Controller에서는 User 타입으로 받고 userType이 ADMIN인지 확인
 * - 관리자 전용 기능에서만 Admin 엔티티 조회
 * - 예: if ("ADMIN".equals(user.getUserType())) { // 관리자 기능 수행 }
 */
@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    
    @Column(name = "employee_id")
    private String employeeId;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "access_level")
    private Integer accessLevel = 1;
    
    public Admin() {}
    
    public Admin(String userid, String password, String nickname, String email) {
        super(userid, password, nickname, email);
    }
    
    // Getters
    public String getEmployeeId() { return employeeId; }
    public String getDepartment() { return department; }
    public Integer getAccessLevel() { return accessLevel; }
    
    // Setters
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setDepartment(String department) { this.department = department; }
    public void setAccessLevel(Integer accessLevel) { this.accessLevel = accessLevel; }
}