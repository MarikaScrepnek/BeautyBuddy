import jakarta.persistence.*;
import java.io.Serializable;
import com.beautybuddy.model.Product;
import com.beautybuddy.model.Ingredient;

@Entity
@Table (name = "may_contain_ingredient"
    UniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "ingredient_id"})
)
public class MayContainIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int may_contain_ingredient_id;

    @ManyToOne(nullable = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(nullable = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    public MayContainIngredient() {}

    public int getMay_contain_ingredient_id() { return may_contain_ingredient_id; }
    public void setMay_contain_ingredient_id(int may_contain_ingredient_id) { this.may_contain_ingredient_id = may_contain_ingredient_id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
}