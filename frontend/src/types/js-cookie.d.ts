declare module 'js-cookie' {
    interface CookiesStatic {
      get(name: string): string | undefined;
      set(name: string, value: string, options?: { expires?: number | Date }): void;
      remove(name: string): void;
    }
  
    const Cookies: CookiesStatic;
    export default Cookies;
  }