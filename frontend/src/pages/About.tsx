import './About.css';
import { Target, Zap, Shield, Users } from 'lucide-react';
import { useSiteSetting } from '../contexts/SiteSettingContext';

export const AboutContent = ({ setting }: { setting: any }) => {
    if (setting?.aboutAdvancedMode) {
        return (
            <div className="about-page about-advanced" style={{ minHeight: '100%', width: '100%' }}>
                <style dangerouslySetInnerHTML={{ __html: setting?.aboutCustomCss || '' }} />
                <div dangerouslySetInnerHTML={{ __html: setting?.aboutCustomHtml || '' }} />
            </div>
        );
    }

    const sectionOrder = setting?.aboutSectionOrder || 'hero,vision,values';
    const sections = sectionOrder.split(',');

    return (
        <div className="about-page" style={{ minHeight: '100%', width: '100%' }}>
            {sections.map((section: string) => {
                if (section === 'hero') {
                    return (
                        <div key="hero" className="about-hero">
                            <div className="about-hero-bg"></div>
                            <div className="container about-hero-content">
                                <h1 className="about-title">{setting?.aboutHeroTitle || '회사소개'}</h1>
                                <p className="about-subtitle" dangerouslySetInnerHTML={{ __html: setting?.aboutHeroSubtitle?.replace(/\\n/g, '<br/>') || '세상을 연결하는 새로운 디지털 경험,<br />Lumière가 만들어갑니다.' }}></p>
                            </div>
                        </div>
                    );
                }

                if (section === 'vision') {
                    return (
                        <section key="vision" className="about-section dark-section">
                            <div className="container">
                                <div className="about-grid">
                                    <div className="about-text-content">
                                        <h2 className="section-title">{setting?.aboutVisionTitle || 'Our Vision'}</h2>
                                        <p className="section-desc">
                                            {setting?.aboutVisionDesc1 || '우리는 단순히 기술을 제공하는 것을 넘어, 사람과 사람, 비즈니스와 가치를 잇는 다리가 되고자 합니다. 한계를 뛰어넘는 혁신적인 플랫폼을 통해 모두가 더 나은 내일을 꿈꿀 수 있도록 만듭니다.'}
                                        </p>
                                        <p className="section-desc">
                                            {setting?.aboutVisionDesc2 || '끊임없는 도전과 열정으로 업계의 표준을 새롭게 정의하며, 사용자 중심의 철학을 바탕으로 최상의 디지털 프로덕트를 구현해 나갑니다.'}
                                        </p>
                                    </div>
                                    <div className="about-image-wrapper">
                                        <div className="glass-card about-stat-card">
                                            <div className="stat-item">
                                                <h3>{setting?.aboutExperienceYears || '10+'}</h3>
                                                <p>Years of Experience</p>
                                            </div>
                                            <div className="stat-divider"></div>
                                            <div className="stat-item">
                                                <h3>{setting?.aboutGlobalPartners || '200+'}</h3>
                                                <p>Global Partners</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </section>
                    );
                }

                if (section === 'values') {
                    return (
                        <section key="values" className="about-section glass-section">
                            <div className="container">
                                <div className="about-header-center">
                                    <h2 className="section-title">Core Values</h2>
                                    <p className="section-subtitle">Lumière가 지켜가는 4가지 핵심 가치</p>
                                </div>

                                <div className="values-grid">
                                    <div className="value-card">
                                        <div className="value-icon"><Target size={32} /></div>
                                        <h3>Customer First</h3>
                                        <p>모든 결정의 중심에는 고객이 있습니다. 최상의 만족을 위해 항상 고객의 목소리에 귀 기울입니다.</p>
                                    </div>
                                    <div className="value-card">
                                        <div className="value-icon"><Zap size={32} /></div>
                                        <h3>Innovation</h3>
                                        <p>안주하지 않고 끊임없이 새로운 기술과 디자인을 연구하여 시장을 선도하는 혁신을 이룹니다.</p>
                                    </div>
                                    <div className="value-card">
                                        <div className="value-icon"><Shield size={32} /></div>
                                        <h3>Trust & Safety</h3>
                                        <p>견고한 보안과 투명한 프로세스를 바탕으로 파트너와 고객 모두에게 무한한 신뢰를 제공합니다.</p>
                                    </div>
                                    <div className="value-card">
                                        <div className="value-icon"><Users size={32} /></div>
                                        <h3>Collaboration</h3>
                                        <p>다양한 전문가들이 모여 자유롭게 소통하고 협력하며 최고의 시너지를 창출합니다.</p>
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

const About = () => {
    const { setting } = useSiteSetting();

    return <AboutContent setting={setting} />;
};

export default About;
