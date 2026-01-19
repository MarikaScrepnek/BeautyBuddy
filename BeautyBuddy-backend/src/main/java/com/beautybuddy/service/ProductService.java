import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.beautybuddy.model.Product;
import com.beautybuddy.model.Ingredient;
import com.beautybuddy.model.ProductIngredient;
import com.beautybuddy.repository.ProductRepository;
import com.beautybuddy.repository.IngredientRepository;
import com.beautybuddy.repository.ProductIngredientRepository;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepo;
    private final IngredientRepository ingredientRepo;
    private final ProductIngredientRepository productIngredientRepo;

    public ProductService(ProductRepository productRepo,
                          IngredientRepository ingredientRepo,
                          ProductIngredientRepository productIngredientRepo) {
        this.productRepo = productRepo;
        this.ingredientRepo = ingredientRepo;
        this.productIngredientRepo = productIngredientRepo;
    }

    public Product createProduct(
            Product product,
            String rawIngredients
    ) {
        Product savedProduct = productRepo.save(product);

        String[] ingredients = rawIngredients.split(",");

        for (String ing : ingredients) {
            String normalized = normalize(ing);

            Ingredient ingredient = ingredientRepo
                .findByNormalizedName(normalized)
                .orElseGet(() -> {
                    Ingredient newIng = new Ingredient();
                    newIng.setName(ing.trim());
                    newIng.setNormalizedName(normalized);
                    return ingredientRepo.save(newIng);
                });

            ProductIngredient pi = new ProductIngredient();
            pi.setProduct(savedProduct);
            pi.setIngredient(ingredient);

            productIngredientRepo.save(pi);
        }

        return savedProduct;
    }

    private String normalize(String name) {
        return name.trim().toLowerCase().replaceAll("[^a-z0-9 ]", "");
    }
}
