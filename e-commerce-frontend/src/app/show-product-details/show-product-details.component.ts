// import { HttpClient, HttpErrorResponse } from '@angular/common/http';
// import { Component, OnInit } from '@angular/core';
// import { MatDialog } from '@angular/material/dialog';
// import { Router } from '@angular/router';
// import { map } from 'rxjs/operators';
// import { ImageProcessingService } from '../image-processing.service';
// import { ShowProductImagesDialogComponent } from '../show-product-images-dialog/show-product-images-dialog.component';
// import { Product } from '../_model/product.model';
// import { ProductService } from '../_services/product.service';
// import { saveAs } from 'file-saver';

// @Component({
//   selector: 'app-show-product-details',
//   templateUrl: './show-product-details.component.html',
//   styleUrls: ['./show-product-details.component.css']
// })
// export class ShowProductDetailsComponent implements OnInit {

//   showLoadMoreProductButton = false;
//   showTable = false;
//   pageNumber: number = 0;
//   productDetails: Product[] = [];
//   displayedColumns: string[] = ['Id', 'Product Name', 'Category', 'description', 'Product Discounted Price', 'Product Actual Price', 'Actions'];

//   constructor(private productService: ProductService,
//     public imagesDialog: MatDialog,
//     private imageProcessingService: ImageProcessingService,
//     private http: HttpClient,
//     private router: Router) { }

//   ngOnInit(): void {
//     this.getAllProducts();
//   }

//   getReport() {
//     this.http.get('http://localhost:9090/pdf', { responseType: 'blob' }).subscribe(res => {
//       const file = new Blob([res], { type: 'application/pdf' });
//       saveAs(file, 'ProductsList.pdf');
//     });
//   }

//   searchByKeyword(searchkeyword) {
//     console.log(searchkeyword);
//     this.pageNumber = 0;
//     this.productDetails = [];
//     this.getAllProducts(searchkeyword);
//   }

//   public getAllProducts(searchKeyword: string = "") {
//     this.showTable = false;
//     this.productService.getAllProducts(this.pageNumber, searchKeyword)
//     .pipe(
//       map((x: Product[], i) => x.map((product: Product) => this.imageProcessingService.createImages(product)))
//     )
//     .subscribe(
//       (resp: Product[]) => {
//         // console.log(resp);
//         resp.forEach(product => this.productDetails.push(product));
//         console.log('msg', this.productDetails);
//         this.showTable = true;

//         if(resp.length == 12) {
//           this.showLoadMoreProductButton = true;
//         } else {
//           this.showLoadMoreProductButton = false;
//         }

//         // this.productDetails = resp;
//       }, (error: HttpErrorResponse) => {
//         console.log(error);
//       }
//     );
//   }

//   loadMoreProduct() {
//     this.pageNumber = this.pageNumber + 1;
//     this.getAllProducts();
//   }

//   deleteProduct(productId) {
//     this.productService.deleteProduct(productId).subscribe(
//       (resp) => {
//         this.getAllProducts();
//       },
//       (error:HttpErrorResponse) => {
//         console.log(error);
//       }
//     );
//   }

//   showImages(product: Product) {
//     console.log(product);
//     this.imagesDialog.open(ShowProductImagesDialogComponent, {
//       data: {
//         images: product.productImages
//       },
//       height: '500px',
//       width: '800px'
//     });
//   }

//   editProductDetails(productId) {
//     this.router.navigate(['/addNewProduct', {productId: productId}]);
//   }
// }

import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { saveAs } from 'file-saver';
import { Product } from '../_model/product.model';
import { ProductService } from '../_services/product.service';
import { ImageProcessingService } from '../image-processing.service';
import { ShowProductImagesDialogComponent } from '../show-product-images-dialog/show-product-images-dialog.component';

@Component({
  selector: 'app-show-product-details',
  templateUrl: './show-product-details.component.html',
  styleUrls: ['./show-product-details.component.css']
})
export class ShowProductDetailsComponent implements OnInit, OnDestroy {
  showLoadMoreProductButton = false;
  showTable = false;
  pageNumber: number = 0;
  productDetails = new MatTableDataSource<Product>([]);
  displayedColumns: string[] = ['Id', 'Product Name', 'Category', 'description', 'Product Discounted Price', 'Product Actual Price', 'Actions'];
  private subscription: Subscription = new Subscription();

  constructor(
    private productService: ProductService,
    public imagesDialog: MatDialog,
    private imageProcessingService: ImageProcessingService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getAllProducts();
  }

  getReport() {
    this.productService.getProductsReport().subscribe({
      next: (res) => {
        const file = new Blob([res], { type: 'application/pdf' });
        saveAs(file, 'ProductsList.pdf');
      },
      error: (error: HttpErrorResponse) => {
        console.error('Erreur lors de la génération du rapport', error);
      }
    });
  }

  searchByKeyword(searchKeyword: string) {
    console.log('Mot-clé de recherche:', searchKeyword);
    this.pageNumber = 0;
    this.productDetails.data = []; // Réinitialiser les données
    this.getAllProducts(searchKeyword);
  }

  public getAllProducts(searchKeyword: string = "") {
    this.showTable = false;
    const sub = this.productService.getAllProducts(this.pageNumber, searchKeyword)
      .pipe(
        map((products: Product[]) => products.map(product => this.imageProcessingService.createImages(product)))
      )
      .subscribe({
        next: (resp: Product[]) => {
          if (this.pageNumber === 0) {
            this.productDetails.data = resp; // Remplacer les données pour la première page
          } else {
            this.productDetails.data = [...this.productDetails.data, ...resp]; // Ajouter pour les pages suivantes
          }
          console.log('Produits chargés:', this.productDetails.data);
          this.showTable = true;
          this.showLoadMoreProductButton = resp.length === 12; // Afficher le bouton si 12 produits
        },
        error: (error: HttpErrorResponse) => {
          console.error('Erreur lors du chargement des produits', error);
          this.showTable = true; // Afficher la table même en cas d'erreur
        }
      });
    this.subscription.add(sub);
  }

  loadMoreProduct() {
    this.pageNumber++;
    this.getAllProducts();
  }

  deleteProduct(productId: number) {
    const sub = this.productService.deleteProduct(productId).subscribe({
      next: () => {
        // Mettre à jour le dataSource localement
        this.productDetails.data = this.productDetails.data.filter(product => product.productId !== productId);
        console.log('Produit supprimé, nouvelle liste:', this.productDetails.data);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Erreur lors de la suppression du produit', error);
      }
    });
    this.subscription.add(sub);
  }

  showImages(product: Product) {
    console.log('Affichage des images:', product.productImages);
    this.imagesDialog.open(ShowProductImagesDialogComponent, {
      data: {
        images: product.productImages
      },
      height: '500px',
      width: '800px'
    });
  }

  editProductDetails(productId: number) {
    this.router.navigate(['/addNewProduct', { productId }]); // Correction: productId au lieu de productid
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe(); // Nettoyer les abonnements
  }

  // Fonction trackBy pour optimiser le rendu
  trackByProductId(index: number, product: Product): number {
    return product.productId;
  }
}