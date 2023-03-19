package ua.com.anna_shop.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ORDERS")
public class Order {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private Long id;

  @JsonProperty(value = "user_session_hash_code")
  @Column(name = "user_session_hash_code")
  private Integer userSessionHashCode;

  @JsonProperty(value = "customer")
  @Column(name = "customer")
  private String customer;

  @JsonProperty(value = "order_details")
  @Column(name = "order_details")
  private String orderDetails;

  @JsonProperty(value = "prepayment")
  @Column(name = "prepayment")
  private int prepayment;

  @JsonProperty(value = "cash_on_delivery")
  @Column(name = "cash_on_delivery")
  private int cashOnDelivery;

  @JsonProperty(value = "total_cost")
  @Column(name = "total_cost")
  private int totalCost;

  @JsonProperty(value = "salary")
  @Column(name = "salary")
  private int salary;

  @JsonProperty(value = "quantity")
  @Column(name = "quantity")
  private int quantity;

  public Order() {
  }

  public Long getId() {
    return id;
  }

  public Integer getUserSessionHashCode() {
    return userSessionHashCode;
  }

  public String getCustomer() {
    return customer;
  }

  public String getOrderDetails() {
    return orderDetails;
  }

  public int getPrepayment() {
    return prepayment;
  }

  public int getCashOnDelivery() {
    return cashOnDelivery;
  }

  public int getTotalCost() {
    return totalCost;
  }

  public int getSalary() {
    return salary;
  }

  public int getQuantity() {
    return quantity;
  }

  public Order setId(Long id) {
    this.id = id;
    return this;
  }

  public Order setUserSessionHashCode(Integer userSessionHashCode) {
    this.userSessionHashCode = userSessionHashCode;
    return this;
  }

  public Order setCustomer(String customer) {
    this.customer = customer;
    return this;
  }

  public Order setOrderDetails(String orderDetails) {
    this.orderDetails = orderDetails;
    return this;
  }

  public Order setPrepayment(int prepayment) {
    this.prepayment = prepayment;
    return this;
  }

  public Order setCashOnDelivery(int cashOnDelivery) {
    this.cashOnDelivery = cashOnDelivery;
    return this;
  }

  public Order setTotalCost(int totalCost) {
    this.totalCost = totalCost;
    return this;
  }

  public Order setSalary(int salary) {
    this.salary = salary;
    return this;
  }

  public Order setQuantity(int quantity) {
    this.quantity = quantity;
    return this;
  }

  @Override
  public String toString() {
    return "Order{" +
        "id=" + id +
        ", userSessionHashCode=" + userSessionHashCode +
        ", customer='" + customer + '\'' +
        ", orderDetails='" + orderDetails + '\'' +
        ", prepayment=" + prepayment +
        ", cashOnDelivery=" + cashOnDelivery +
        ", totalCost=" + totalCost +
        ", salary=" + salary +
        ", quantity=" + quantity +
        '}';
  }
}
