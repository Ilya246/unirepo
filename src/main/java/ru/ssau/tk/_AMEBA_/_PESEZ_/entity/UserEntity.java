package ru.ssau.tk._AMEBA_._PESEZ_.entity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_Id",nullable = false)
    private Long userId;

    @Column(name = "type_Id", columnDefinition = "INT CHECK (type_Id >= 1 AND type_Id <= 2)")
    private int typeId;

    @Column(name = "user_Name", length = 100)
    private String userName;

    @Column(name = "password", length = 20)
    private String password;

    @Column(name = "created_Date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FunctionOwnershipEntity> functionOwnerships = new ArrayList<>();

    // Constructors
    public UserEntity() {}

    public UserEntity(int typeId, String userName, String password, Date createdDate) {
        setTypeId(typeId);
        this.userName = userName;
        this.password = password;
        this.createdDate = createdDate;
    }


    public UserEntity(int typeId, String userName, String password) {
        this(typeId, userName, password, new Date());
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    //public void setUserId(int userId) { this.userId = userId; }

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) {
        if (typeId < 1 || typeId > 2) {
            throw new IllegalArgumentException("typeId must be between 1 and 2");
        }
        this.typeId = typeId;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public List<FunctionOwnershipEntity> getFunctionOwnerships() { return functionOwnerships; }
    public void setFunctionOwnerships(List<FunctionOwnershipEntity> functionOwnerships) {
        this.functionOwnerships = functionOwnerships;
    }

    // Дополнительные методы для удобства
    public boolean isValidType() {
        return typeId >= 1 && typeId <= 2;
    }

    public boolean isType1() {
        return typeId == 1;
    }

    public boolean isType2() {
        return typeId == 2;
    }

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = new Date();
        }
    }

    @Override
    public String toString() {
        return "UserEntity{userId=" + userId + ", typeId=" + typeId +
                ", userName='" + userName + "', createdDate=" + createdDate + "}";
    }
}
