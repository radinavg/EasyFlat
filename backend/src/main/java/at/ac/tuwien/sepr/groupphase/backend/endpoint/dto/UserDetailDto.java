package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UserDetailDto {

    private Long id;

    @NotBlank(message = "First name cannot be empty")
    @Size(max = 100, message = "The first name cannot be larger than 100 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 100, message = "The last name cannot be larger than 100 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email is not in a valid form")
    @Size(max = 100, message = "The email cannot be larger than 100 characters")
    private String email;

    private String flatName;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "The password must be at least 8 characters")
    @Size(max = 100, message = "The password cannot be larger than 100 characters")
    private String password;

    private boolean admin;

    private Integer points;

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFlatName() {
        return flatName;
    }

    public void setFlatName(String flatName) {
        this.flatName = flatName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDetailDto that = (UserDetailDto) o;
        return Objects.equals(firstName, that.firstName)
            && Objects.equals(lastName, that.lastName)
            && Objects.equals(email, that.email)
            && Objects.equals(flatName, that.flatName)
            && Objects.equals(password, that.password)
            && Objects.equals(admin, that.admin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, flatName, password, admin);
    }

    @Override
    public String toString() {
        return "UserDetailDto{"
            + "firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", email='" + email + '\''
            + ", flatName='" + flatName + '\''
            + ", password='" + password + '\''
            + ", admin=" + admin
            + '}';
    }
}
