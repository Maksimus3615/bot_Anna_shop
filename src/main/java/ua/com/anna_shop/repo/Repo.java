package ua.com.anna_shop.repo;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.anna_shop.models.Order;

@Repository
public interface Repo extends JpaRepository<Order, Long> {
  ArrayList<Order> findAllByUserSessionHashCode(Integer userSessionHashCode);
}
