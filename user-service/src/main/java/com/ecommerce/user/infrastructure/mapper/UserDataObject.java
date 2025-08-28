package com.ecommerce.user.infrastructure.mapper;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;

import java.time.LocalDateTime;

/**
 * 用户数据对象 - 对应数据库表结构
 * 支持DDD设计：值对象分拆存储、乐观锁、软删除
 */
@TableName("users")
public class UserDataObject {

    @TableId(value = "user_id", type = IdType.ASSIGN_UUID)
    private String userId;

    @TableField("username")
    private String username;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("phone_country_code")
    private String phoneCountryCode;

    @TableField("first_name")
    private String firstName;

    @TableField("last_name")
    private String lastName;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("status")
    private String status;

    @TableField("type")
    private String type;

    // 默认地址信息 - 拆分字段存储，便于查询和索引
    @TableField("address_country")
    private String addressCountry;

    @TableField("address_province")
    private String addressProvince;

    @TableField("address_city")
    private String addressCity;

    @TableField("address_district")
    private String addressDistrict;

    @TableField("address_street")
    private String addressStreet;

    @TableField("address_postal_code")
    private String addressPostalCode;

    @TableField("address_contact_name")
    private String addressContactName;

    @TableField("address_contact_phone")
    private String addressContactPhone;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField("created_by")
    private String createdBy;

    @TableField("updated_by")
    private String updatedBy;

    // 乐观锁版本控制
    @Version
    @TableField("version")
    private Integer version;

    // 软删除标记
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    // Constructors
    public UserDataObject() {
        this.deleted = false;
        this.version = 0;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPhoneCountryCode() { return phoneCountryCode; }
    public void setPhoneCountryCode(String phoneCountryCode) { this.phoneCountryCode = phoneCountryCode; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAddressCountry() { return addressCountry; }
    public void setAddressCountry(String addressCountry) { this.addressCountry = addressCountry; }

    public String getAddressProvince() { return addressProvince; }
    public void setAddressProvince(String addressProvince) { this.addressProvince = addressProvince; }

    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String addressCity) { this.addressCity = addressCity; }

    public String getAddressDistrict() { return addressDistrict; }
    public void setAddressDistrict(String addressDistrict) { this.addressDistrict = addressDistrict; }

    public String getAddressStreet() { return addressStreet; }
    public void setAddressStreet(String addressStreet) { this.addressStreet = addressStreet; }

    public String getAddressPostalCode() { return addressPostalCode; }
    public void setAddressPostalCode(String addressPostalCode) { this.addressPostalCode = addressPostalCode; }

    public String getAddressContactName() { return addressContactName; }
    public void setAddressContactName(String addressContactName) { this.addressContactName = addressContactName; }

    public String getAddressContactPhone() { return addressContactPhone; }
    public void setAddressContactPhone(String addressContactPhone) { this.addressContactPhone = addressContactPhone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}
