import { Injectable } from "@angular/core";
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from "@angular/common/http";
import { AuthService } from "../services/auth.service";
import { Observable } from "rxjs";
import { Globals } from "../global/globals";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private globals: Globals) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const whiteList = [
      "/authentication",
      "/register",
      "/account",
      "/wgLogin",
      "/wgCreate",
      "/storage",
      "/cooking",
      "/cookbook",
    ];

    // Do not intercept authentication requests
    if (
      whiteList.find((whiteListedEndpoint) =>
        req.url.startsWith(this.globals.backendUri + whiteListedEndpoint)
      )
    ) {
      return next.handle(req);
    }

    const authReq = req.clone({
      headers: req.headers.set(
        "Authorization",
        "Bearer " + this.authService.getToken()
      ),
    });

    return next.handle(authReq);
  }
}
