export interface AuthenticationService {

    jwt: string;
    url?: string;
    isAuth: boolean;

    setJwtToken(): void;

    setUrl(): void;

    setIsAuth(): Promise<void>;

    isAuthenticated(): Promise<boolean>;

    login(username: string, password: string, callback?: (hasError: boolean, errorMessage: string) => any): void;

    logout(): void;

    cleanStorage(): void;

    handleErrors(): Promise<boolean>;

}