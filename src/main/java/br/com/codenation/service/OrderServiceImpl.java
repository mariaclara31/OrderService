package br.com.codenation.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

import static java.util.stream.Collectors.groupingBy;

public class OrderServiceImpl implements OrderService {

		private final ProductRepository productRepository = new ProductRepositoryImpl();
		private static final double DISCOUNT_VALUE = 0.2;

		/**
		 * Calculate the sum of all OrderItems
		 */
		@Override
		public Double calculateOrderValue(List<OrderItem> items) {
			return items.stream()
					.mapToDouble(orderItem -> {
						Product product = productRepository.findById(orderItem.getProductId())
								.orElseGet(null);
						if(product != null) {
							double grossAmount = product.getValue()*orderItem.getQuantity();
							return product.getIsSale()? grossAmount * (1 - DISCOUNT_VALUE): grossAmount;
						}
						return 0;
					}).sum();
		}

		/**
		 * Map from idProduct List to Product Set
		 */
		@Override
		public Set<Product> findProductsById(List<Long> ids) {
			return ids.stream()
					.map(id -> productRepository.findById(id)
							.orElse(null))
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());

		}

		/**
		 * Calculate the sum of all Orders(List<OrderIten>)
		 */
		@Override
		public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
			return orders.stream()
				.mapToDouble(this::calculateOrderValue).sum();
		}

		/**
		 * Group products using isSale attribute as the map key
		 */
		@Override
		public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {

			List<Product> products = productRepository.findAll().stream()
					.filter(p -> productIds.contains(p.getId()))
					.collect(Collectors.toList());

			return products.stream()
					.collect(groupingBy(Product::getIsSale));
		}
}

