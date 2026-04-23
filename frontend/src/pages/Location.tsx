import './Location.css';
import { MapPin, Phone, Mail, Clock, Train, Bus } from 'lucide-react';
import { useSiteSetting } from '../contexts/SiteSettingContext';

const extractIframeSrc = (input: string): string => {
    if (!input) return '';
    const match = input.match(/src=["']([^"']+)["']/);
    return match ? match[1] : input.trim();
};

export const LocationContent = ({ setting }: { setting: any }) => {
    if (setting?.locationAdvancedMode) {
        return (
            <div className="location-page location-advanced" style={{ minHeight: '100%', width: '100%' }}>
                <style dangerouslySetInnerHTML={{ __html: setting?.locationCustomCss || '' }} />
                <div dangerouslySetInnerHTML={{ __html: setting?.locationCustomHtml || '' }} />
            </div>
        );
    }

    const sectionOrder = setting?.locationSectionOrder || 'hero,map,info,transport';
    const sections = sectionOrder.split(',');

    return (
        <div className="location-page" style={{ minHeight: '100%', width: '100%' }}>
            {sections.map((section: string) => {
                if (section === 'hero') {
                    return (
                        <div key="hero" className="location-hero">
                            <div className="location-hero-bg"></div>
                            <div className="container location-hero-content">
                                <h1 className="location-title">오시는 길</h1>
                                <p className="location-subtitle">언제든 환영합니다. 방문 전 편하게 문의해주세요.</p>
                            </div>
                        </div>
                    );
                }

                if (section === 'map') {
                    const provider = setting?.locationMapProvider || 'google';
                    const iframeSrc = provider === 'kakao'
                        ? extractIframeSrc(setting?.locationKakaoMapIframe || '')
                        : extractIframeSrc(setting?.locationMapIframe || "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3164.7171442007466!2d127.0305886!3d37.5146059!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x357ca3f707f4ebfb%3A0xeebd12df387a3cb1!2sGangnam-gu%2C%20Seoul!5e0!3m2!1sen!2skr!4v1700000000000!5m2!1sen!2skr");

                    return (
                        <section key="map" className="location-section map-section" style={{ paddingBottom: 0 }}>
                            <div className="container">
                                <div className="map-container glass-card">
                                    {iframeSrc ? (
                                        <iframe
                                            title="Office Location Map"
                                            src={iframeSrc}
                                            width="100%"
                                            height="100%"
                                            style={{ border: 0 }}
                                            allowFullScreen={false}
                                            loading="lazy"
                                            referrerPolicy="no-referrer-when-downgrade"
                                            className="map-iframe"
                                        ></iframe>
                                    ) : (
                                        <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>
                                            지도 설정이 필요합니다.
                                        </div>
                                    )}
                                </div>
                            </div>
                        </section>
                    );
                }

                if (section === 'info') {
                    return (
                        <section key="info" className="location-section map-section" style={{ paddingTop: '3rem' }}>
                            <div className="container">
                                <div className="info-grid">
                                    <div className="info-card">
                                        <div className="info-icon"><MapPin size={24} /></div>
                                        <h3>본사 주소</h3>
                                        <p dangerouslySetInnerHTML={{ __html: (setting?.locationAddress || '서울특별시 강남구 테헤란로 123\\nLumière 타워 15층').replace(/\\n/g, '<br />') }}></p>
                                        <span className="info-sub">(우) 06234</span>
                                    </div>
                                    <div className="info-card">
                                        <div className="info-icon"><Phone size={24} /></div>
                                        <h3>전화번호</h3>
                                        <p dangerouslySetInnerHTML={{ __html: (setting?.locationPhone || '대표전화: 02-1234-5678\\n고객센터: 1588-0000').replace(/\\n/g, '<br />') }}></p>
                                        <span className="info-sub">평일 09:00 - 18:00</span>
                                    </div>
                                    <div className="info-card">
                                        <div className="info-icon"><Mail size={24} /></div>
                                        <h3>이메일</h3>
                                        <p dangerouslySetInnerHTML={{ __html: (setting?.locationEmail || '제휴/문의: contact@lumiere.com\\n채용: hr@lumiere.com').replace(/\\n/g, '<br />') }}></p>
                                        <span className="info-sub">24시간 내 회신</span>
                                    </div>
                                    <div className="info-card">
                                        <div className="info-icon"><Clock size={24} /></div>
                                        <h3>운영 시간</h3>
                                        <p dangerouslySetInnerHTML={{ __html: (setting?.locationHours || '평일: AM 09:00 - PM 18:00\\n점심: PM 12:30 - PM 13:30').replace(/\\n/g, '<br />') }}></p>
                                        <span className="info-sub">주말 및 공휴일 휴무</span>
                                    </div>
                                </div>
                            </div>
                        </section>
                    );
                }

                if (section === 'transport') {
                    return (
                        <section key="transport" className="location-section transport-section">
                            <div className="container">
                                <h2 className="section-title text-center">대중교통 안내</h2>
                                <div className="transport-grid">
                                    <div className="transport-card">
                                        <div className="transport-header">
                                            <div className="transport-icon subway"><Train size={24} /></div>
                                            <h3>지하철 이용 시</h3>
                                        </div>
                                        <ul className="transport-list">
                                            <li>
                                                <span className="line-badge line-2">2호선</span>
                                                <strong>강남역</strong> 1번 출구 도보 5분
                                            </li>
                                            <li>
                                                <span className="line-badge line-shinbundang">신분당선</span>
                                                <strong>강남역</strong> 3번 출구 도보 7분
                                            </li>
                                        </ul>
                                    </div>
                                    <div className="transport-card">
                                        <div className="transport-header">
                                            <div className="transport-icon bus"><Bus size={24} /></div>
                                            <h3>버스 이용 시</h3>
                                        </div>
                                        <ul className="transport-list">
                                            <li>
                                                <span className="bus-badge blue">간선</span>
                                                146, 341, 360, 740
                                            </li>
                                            <li>
                                                <span className="bus-badge green">지선</span>
                                                1124, 3412, 4412
                                            </li>
                                            <li>
                                                <span className="bus-badge red">직행</span>
                                                1005, 1100, 1700
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </section>
                    );
                }
                return null;
            })}
        </div>
    );
};

const Location = () => {
    const { setting } = useSiteSetting();

    return <LocationContent setting={setting} />;
};

export default Location;
