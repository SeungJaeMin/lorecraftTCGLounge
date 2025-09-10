package com.lorecraft.tcglounge.entity;

import jakarta.persistence.*;

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