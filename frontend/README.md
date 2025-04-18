# Secret Stuffs Frontend Service

This is the frontend service for Secret Stuffs platform, built with React and TypeScript. It provides a modern, responsive user interface for item donations and claims management, real-time chat, and user profile management.

## Technology Stack

- **Framework**: React 18
- **Language**: TypeScript 5.5
- **Build Tool**: Vite 5.4
- **Package Manager**: Bun
- **UI Library**: Ant Design 5.21
- **State Management**: React Query
- **Routing**: React Router 6
- **Styling**: Tailwind CSS
- **Real-time**: WebSocket (SockJS + STOMP)
- **File Storage**: Firebase Storage
- **HTTP Client**: Axios

## Project Structure
```bash
frontend
├── src
│ ├── assets # Static assets (images, icons)
│ ├── components # Reusable UI components
│ │ ├── forms # Form components
│ │ └── common # Common UI elements
│ ├── context # React context providers
│ ├── hooks # Custom React hooks
│ ├── layout # Layout components
│ ├── services # API services and external integrations
│ │ ├── auth # Authentication services
│ │ ├── user # User management services
│ │ ├── item # Item/donation services
│ │ └── files # File upload services
│ ├── types # TypeScript type definitions
│ └── utils # Utility functions
├── public # Public static files
└── dist # Build output
```

## Key Features

- User authentication with JWT
- Dark/Light theme support
- Responsive design for mobile and desktop
- Real-time chat implementation
- File upload with Firebase Storage
- Form validation and error handling
- Dynamic routing
- API integration with backend services
- Social media sharing
- Search and filtering capabilities

## Environment Configuration

The application supports multiple environment configurations through `.env` files:

```bash
VITE_API_BASE_URL=http://localhost:8082
VITE_FIREBASE_API_KEY=your_firebase_api_key
VITE_FIREBASE_AUTH_DOMAIN=your_firebase_auth_domain
VITE_FIREBASE_PROJECT_ID=your_firebase_project_id
VITE_FIREBASE_STORAGE_BUCKET=your_firebase_storage_bucket
VITE_FIREBASE_MESSAGING_SENDER_ID=your_firebase_messaging_sender_id
VITE_FIREBASE_APP_ID=your_firebase_app_id
```

## Development Setup

### Local Development
1. Install dependencies:
```bash
bun install
```

2. Start development server:
```bash
bun run dev
```

3. Build for production:
```bash
bun run build
```

### Docker Development
1. Using Docker Compose (recommended):
```bash
# Start all services
docker compose up

# Start only frontend
docker compose up frontend
```

2. Using Docker directly:
```bash
# Build the image
docker build -t secret-stuffs-frontend .

# Run the container
docker run -p 3000:3000 \
  -e VITE_API_BASE_URL=/api \
  -e VITE_WEBSOCKET_BASE_URL=/ws \
  secret-stuffs-frontend
```

The application will be available at:
- Local development: http://localhost:3000
- Docker development: http://localhost:80 (through nginx reverse proxy)

### Environment Variables
When running with Docker, the following environment variables are automatically set:
```bash
VITE_API_BASE_URL=/api        # API endpoint through nginx reverse proxy
VITE_WEBSOCKET_BASE_URL=/ws   # WebSocket endpoint through nginx reverse proxy
```

For local development, create a `.env` file with:
```bash
VITE_API_BASE_URL=http://localhost:8082
VITE_FIREBASE_API_KEY=your_firebase_api_key
VITE_FIREBASE_AUTH_DOMAIN=your_firebase_auth_domain
VITE_FIREBASE_PROJECT_ID=your_firebase_project_id
VITE_FIREBASE_STORAGE_BUCKET=your_firebase_storage_bucket
VITE_FIREBASE_MESSAGING_SENDER_ID=your_firebase_messaging_sender_id
VITE_FIREBASE_APP_ID=your_firebase_app_id
```

## Code Quality

- TypeScript for type safety
- ESLint for code linting
- Prettier for code formatting
- Tailwind CSS for consistent styling
- Component-based architecture
- Modular file structure

## Performance Optimization

- Code splitting with Vite
- Lazy loading of components
- Image optimization
- Efficient bundle size management
- Caching strategies

## Security

- JWT token management
- Secure file upload handling
- Environment variable protection
- CORS configuration
- Input sanitization

## Contributing

Please refer to the main project repository's contributing guidelines.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
