import com.beautybuddy.model.Ingredient;
import com.beautybuddy.model.Product;
import com.beautybuddy.model.ProductMaybeIngredient;
import com.beautybuddy.model.keys.ProductIngredientKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMaybeIngredientRepository extends JpaRepository<ProductMaybeIngredient, ProductIngredientKey> {
    boolean existsByProductAndIngredient(Product product, Ingredient ingredient);
}
