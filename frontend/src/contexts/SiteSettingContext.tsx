import React, { createContext, useContext, useState, useEffect } from 'react';

export interface SiteSetting {
    siteName: string;
    metaTitle: string;
    metaDescription: string;
    metaKeywords: string;
    ogTitle: string;
    ogDescription: string;
    ogImage: string;
    logoUrl: string;
    logoAltText: string;
    footerCopyright: string;
    canonicalUrl: string;
    googleAnalyticsId: string;
    robotsTxt: string;

    // About
    aboutHeroTitle?: string;
    aboutHeroSubtitle?: string;
    aboutVisionTitle?: string;
    aboutVisionDesc1?: string;
    aboutVisionDesc2?: string;
    aboutExperienceYears?: string;
    aboutGlobalPartners?: string;

    // Location
    locationAddress?: string;
    locationPhone?: string;
    locationEmail?: string;
    locationHours?: string;
    locationMapIframe?: string;
}

interface SiteSettingContextType {
    setting: SiteSetting | null;
}

const SiteSettingContext = createContext<SiteSettingContextType>({ setting: null });

export const useSiteSetting = () => useContext(SiteSettingContext);

export const SiteSettingProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [setting, setSetting] = useState<SiteSetting | null>(null);

    useEffect(() => {
        const fetchSetting = async () => {
            try {
                const res = await fetch(`http://${window.location.hostname}:8080/api/global/setting`);
                if (res.ok) {
                    const data = await res.json();
                    setSetting(data);

                    // 문서 헤더(Document Head) 동적 업데이트
                    if (data.metaTitle || data.siteName) {
                        document.title = data.metaTitle || data.siteName;
                    }
                    if (data.metaDescription) {
                        let metaDesc = document.querySelector('meta[name="description"]');
                        if (!metaDesc) {
                            metaDesc = document.createElement('meta');
                            metaDesc.setAttribute('name', 'description');
                            document.head.appendChild(metaDesc);
                        }
                        metaDesc.setAttribute('content', data.metaDescription);
                    }
                    if (data.metaKeywords) {
                        let metaKeywords = document.querySelector('meta[name="keywords"]');
                        if (!metaKeywords) {
                            metaKeywords = document.createElement('meta');
                            metaKeywords.setAttribute('name', 'keywords');
                            document.head.appendChild(metaKeywords);
                        }
                        metaKeywords.setAttribute('content', data.metaKeywords);
                    }
                    if (data.ogTitle) {
                        let ogTitle = document.querySelector('meta[property="og:title"]');
                        if (!ogTitle) {
                            ogTitle = document.createElement('meta');
                            ogTitle.setAttribute('property', 'og:title');
                            document.head.appendChild(ogTitle);
                        }
                        ogTitle.setAttribute('content', data.ogTitle);
                    }
                    if (data.ogDescription) {
                        let ogDesc = document.querySelector('meta[property="og:description"]');
                        if (!ogDesc) {
                            ogDesc = document.createElement('meta');
                            ogDesc.setAttribute('property', 'og:description');
                            document.head.appendChild(ogDesc);
                        }
                        ogDesc.setAttribute('content', data.ogDescription);
                    }
                    if (data.ogImage) {
                        let ogImage = document.querySelector('meta[property="og:image"]');
                        if (!ogImage) {
                            ogImage = document.createElement('meta');
                            ogImage.setAttribute('property', 'og:image');
                            document.head.appendChild(ogImage);
                        }
                        const imageUrl = data.ogImage.startsWith('http') ? data.ogImage : `http://${window.location.hostname}:8080${data.ogImage}`;
                        ogImage.setAttribute('content', imageUrl);
                    }
                    if (data.canonicalUrl) {
                        let canonical = document.querySelector('link[rel="canonical"]');
                        if (!canonical) {
                            canonical = document.createElement('link');
                            canonical.setAttribute('rel', 'canonical');
                            document.head.appendChild(canonical);
                        }
                        canonical.setAttribute('href', data.canonicalUrl);
                    }
                }
            } catch (err) {
                console.error("Failed to load site settings", err);
            }
        };

        fetchSetting();
    }, []);

    return (
        <SiteSettingContext.Provider value={{ setting }}>
            {children}
        </SiteSettingContext.Provider>
    );
};
