package me.sussydeveloper.utils;

public class MathUtils {

    public static float calculateDiscountedPrice(float originalPrice, float discountPercent) {
        if (originalPrice <= 0 || discountPercent < 0) {
            return originalPrice;
        }
        float discountValue = originalPrice * (discountPercent / 100);
        return originalPrice - discountValue;
    }

}
