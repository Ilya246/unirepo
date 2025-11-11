package ru.ssau.tk._AMEBA_._PESEZ_.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "password") // исключаем пароль из toString
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_Id", nullable = false)
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

    public UserEntity(int typeId, String userName, String password, Date createdDate) {
        setTypeId(typeId);
        this.userName = userName;
        this.password = password;
        this.createdDate = createdDate;
    }

    public UserEntity(int typeId, String userName, String password) {
        this(typeId, userName, password, new Date());
    }

    public void setTypeId(int typeId) {
        if (typeId < 1 || typeId > 2) {
            throw new IllegalArgumentException("typeId must be between 1 and 2");
        }
        this.typeId = typeId;
    }

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = new Date();
        }
    }
}