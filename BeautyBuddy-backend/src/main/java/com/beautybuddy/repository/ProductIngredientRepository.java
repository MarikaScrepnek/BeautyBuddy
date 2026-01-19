import org.springframework.data.jpa.repository.JpaRepository;
import com.beautybuddy.model.ProductIngredient;

import com.beautybuddy.model.Product;
import com.beautybuddy.model.Ingredient;

public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Integer> {
    boolean existsByProductAndIngredient(Product product, Ingredient ingredient);
}