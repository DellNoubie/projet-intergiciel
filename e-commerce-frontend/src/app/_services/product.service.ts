import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { OrderDetails } from '../_model/order-details.model';
import { MyOrderDetails } from '../_model/order.model';
import { Product } from '../_model/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private BASE_URL = 'http://localhost:8090';

  constructor(private httpClient: HttpClient) { }

  public createTransaction(orderId: number, amount: number) {
    return this.httpClient.post(`${this.BASE_URL}/payment/create`, { orderId, amount });
  }

  public verifyPayment(razorpayOrderId: string, razorpayPaymentId: string, razorpaySignature: string) {
    return this.httpClient.post(`${this.BASE_URL}/payment/verify`, {
      razorpayOrderId,
      razorpayPaymentId,
      razorpaySignature
    });
  }

  public markAsDelivered(orderId: number) {
    return this.httpClient.put(`${this.BASE_URL}/orders/admin/${orderId}/status?status=DELIVERED`, {});
  }

  public getAllOrderDetailsForAdmin(status: string): Observable<MyOrderDetails[]> {
    return this.httpClient.get<MyOrderDetails[]>(`${this.BASE_URL}/orders/admin/all`).pipe(
      map((orders: MyOrderDetails[]) => {
        if (!status || status === 'All') return orders;
        return orders.filter(o => o.orderStatus?.toLowerCase() === status.toLowerCase());
      })
    );
  }

  public getMyOrders(): Observable<MyOrderDetails[]> {
    return this.httpClient.get<MyOrderDetails[]>(`${this.BASE_URL}/orders/my`);
  }

  public deleteCartItem(cartId: number) {
    return this.httpClient.delete(`${this.BASE_URL}/cart/${cartId}`);
  }

  public addProduct(product: FormData) {
    return this.httpClient.post<Product>(`${this.BASE_URL}/products`, product);
  }

  public getAllProducts(pageNumber: number, searchKeyword: string = ""): Observable<Product[]> {
    if (searchKeyword) {
      return this.httpClient.get<Product[]>(`${this.BASE_URL}/products/search?name=${searchKeyword}`);
    }
    return this.httpClient.get<any>(`${this.BASE_URL}/products/paged?page=${pageNumber}&size=12`).pipe(
      map(page => page.content || [])
    );
  }

  public getProductDetailsById(productId: number) {
    return this.httpClient.get<Product>(`${this.BASE_URL}/products/${productId}`);
  }

  public deleteProduct(productId: number) {
    return this.httpClient.delete(`${this.BASE_URL}/products/${productId}`);
  }

  public getProductDetails(isSingleProductCheckout: any, productId: any): Observable<Product[]> {
    if (isSingleProductCheckout === true || isSingleProductCheckout === 'true') {
      return this.httpClient.get<Product>(`${this.BASE_URL}/products/${productId}`).pipe(
        map(product => [product])
      );
    }
    return this.httpClient.get<any[]>(`${this.BASE_URL}/cart`).pipe(
      map(cartItems => cartItems.map(item => ({
        productId: item.productId,
        productName: item.productName,
        productDiscountedPrice: item.productPrice,
        productActualPrice: item.productPrice,
        quantity: item.quantity
      } as unknown as Product)))
    );
  }

  public placeOrder(orderDetails: OrderDetails, isCartCheckout: any) {
    return this.httpClient.post(`${this.BASE_URL}/orders/place?fromCart=${isCartCheckout}`, orderDetails);
  }

  public addToCart(productId: number) {
    return this.httpClient.post(`${this.BASE_URL}/cart/add?productId=${productId}`, {});
  }

  public getCartDetails() {
    return this.httpClient.get(`${this.BASE_URL}/cart`);
  }

  public getProductsReport(): Observable<Blob> {
    return this.httpClient.get(`${this.BASE_URL}/reports/products`, { responseType: 'blob' });
  }

  public getOrdersReport(): Observable<Blob> {
    return this.httpClient.get(`${this.BASE_URL}/reports/orders`, { responseType: 'blob' });
  }

  public getInvoice(orderId: number): Observable<Blob> {
    return this.httpClient.get(`${this.BASE_URL}/reports/invoice/${orderId}`, { responseType: 'blob' });
  }
}
