import org.springframework.data.jpa.repository.JpaRepository;
import com.beautybuddy.model.ProductIngredient;

public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Integer> {
}