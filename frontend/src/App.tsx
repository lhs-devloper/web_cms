import { BrowserRouter, Routes, Route } from 'react-router-dom';
import RootLayout from './layouts/RootLayout';
import Home from './pages/Home';
import Login from './pages/Login';
import LoginSuccess from './pages/LoginSuccess';
import MyPage from './pages/MyPage';
import About from './pages/About';
import Location from './pages/Location';
import AdminLayout from './layouts/AdminLayout';
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminBanner from './pages/admin/AdminBanner';
import AdminMenu from './pages/admin/AdminMenu';
import AdminMember from './pages/admin/AdminMember';
import AdminSetting from './pages/admin/AdminSetting';
import AdminAboutSetting from './pages/admin/AdminAboutSetting';
import AdminLocationSetting from './pages/admin/AdminLocationSetting';
import AdminSocialSetting from './pages/admin/AdminSocialSetting';
import AdminPaymentSetting from './pages/admin/AdminPaymentSetting';
import AdminBoards from './pages/admin/AdminBoards';
import AdminProducts from './pages/admin/AdminProducts';
import AdminProductCategory from './pages/admin/AdminProductCategory';
import AdminStory from './pages/admin/AdminStory';
import StoryList from './pages/StoryList';
import StoryDetail from './pages/StoryDetail';
import BoardIndex from './pages/BoardIndex';
import BoardList from './pages/BoardList';
import BoardView from './pages/BoardView';
import BoardWrite from './pages/BoardWrite';
import ProductList from './pages/ProductList';
import ProductDetail from './pages/ProductDetail';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import PaymentSuccess from './pages/PaymentSuccess';
import OrderHistory from './pages/OrderHistory';
import AdminActivityLog from './pages/admin/AdminActivityLog';
import AdminOrder from './pages/admin/AdminOrder';
import AdminMembership from './pages/admin/AdminMembership';
import Membership from './pages/Membership';
import { SiteSettingProvider } from './contexts/SiteSettingContext';
import { ThemeProvider } from './contexts/ThemeContext';
import './App.css';

function App() {
  return (
    <ThemeProvider>
      <SiteSettingProvider>
        <BrowserRouter>
          <Routes>
            {/* 일반 사용자 영역 (Root) */}
            <Route element={<RootLayout />}>
              <Route path="/" element={<Home />} />
              <Route path="/mypage" element={<MyPage />} />
              <Route path="/about" element={<About />} />
              <Route path="/location" element={<Location />} />
              <Route path="/board" element={<BoardIndex />} />
              <Route path="/board/:boardId" element={<BoardList />} />
              <Route path="/board/:boardId/view/:id" element={<BoardView />} />
              <Route path="/board/:boardId/write" element={<BoardWrite />} />
              <Route path="/board/:boardId/edit/:id" element={<BoardWrite />} />
              <Route path="/products" element={<ProductList />} />
              <Route path="/products/:id" element={<ProductDetail />} />
              <Route path="/cart" element={<Cart />} />
              <Route path="/checkout" element={<Checkout />} />
              <Route path="/payment/success" element={<PaymentSuccess />} />
              <Route path="/orders" element={<OrderHistory />} />
              <Route path="/membership" element={<Membership />} />
              <Route path="/stories" element={<StoryList />} />
              <Route path="/stories/:id" element={<StoryDetail />} />
            </Route>

            {/* 단독 페이지 영역 (레이아웃 없는 전체화면) */}
            <Route path="/login" element={<Login />} />
            <Route path="/login/success" element={<LoginSuccess />} />

            {/* 통합 관리자 영역 (CMS / GnuBoard 형태) */}
            <Route path="/admin" element={<AdminLayout />}>
              <Route index element={<AdminDashboard />} />
              <Route path="banners" element={<AdminBanner />} />
              <Route path="menus" element={<AdminMenu />} />
              <Route path="members" element={<AdminMember />} />
              <Route path="settings" element={<AdminSetting />} />
              <Route path="settings/about" element={<AdminAboutSetting />} />
              <Route path="settings/location" element={<AdminLocationSetting />} />
              <Route path="settings/social" element={<AdminSocialSetting />} />
              <Route path="settings/payment" element={<AdminPaymentSetting />} />
              <Route path="boards" element={<AdminBoards />} />
              <Route path="products" element={<AdminProducts />} />
              <Route path="product-categories" element={<AdminProductCategory />} />
              <Route path="stories" element={<AdminStory />} />
              <Route path="activity-log" element={<AdminActivityLog />} />
              <Route path="orders" element={<AdminOrder />} />
              <Route path="membership" element={<AdminMembership />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </SiteSettingProvider>
    </ThemeProvider>
  );
}

export default App;
