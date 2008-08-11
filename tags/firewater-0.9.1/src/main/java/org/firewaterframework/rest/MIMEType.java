package org.firewaterframework.rest;
/*
    Copyright 2008 John TW Spurway
    Licensed under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with the License.
    You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied. See the License for the specific language governing permissions
    and limitations under the License.
*/
/**
 * This class represents the Media-type header field in an HTTP response.  The class provides an
 * exhaustive set of static properties that contain most of the MIME types in common use.
 * @author Tim Spurway
 */
public class MIMEType
{
    public static final MIMEType application_base64 = new MIMEType( "application/base64");
    public static final MIMEType application_binhex = new MIMEType( "application/binhex");
    public static final MIMEType application_binhex4 = new MIMEType( "application/binhex4");
    public static final MIMEType application_excel = new MIMEType( "application/excel");
    public static final MIMEType application_gnutar = new MIMEType( "application/gnutar");
    public static final MIMEType application_groupwise = new MIMEType( "application/groupwise");
    public static final MIMEType application_hlp = new MIMEType( "application/hlp");
    public static final MIMEType application_hta = new MIMEType( "application/hta");
    public static final MIMEType application_i_deas = new MIMEType( "application/i-deas");
    public static final MIMEType application_iges = new MIMEType( "application/iges");
    public static final MIMEType application_inf = new MIMEType( "application/inf");
    public static final MIMEType application_java = new MIMEType( "application/java");
    public static final MIMEType application_java_byte_code = new MIMEType( "application/java-byte-code");
    public static final MIMEType application_lha = new MIMEType( "application/lha");
    public static final MIMEType application_lzx = new MIMEType( "application/lzx");
    public static final MIMEType application_mac_binary = new MIMEType( "application/mac-binary");
    public static final MIMEType application_mac_binhex = new MIMEType( "application/mac-binhex");
    public static final MIMEType application_mac_binhex40 = new MIMEType( "application/mac-binhex40");
    public static final MIMEType application_macbinary = new MIMEType( "application/macbinary");
    public static final MIMEType application_marc = new MIMEType( "application/marc");
    public static final MIMEType application_mbedlet = new MIMEType( "application/mbedlet");
    public static final MIMEType application_mcad = new MIMEType( "application/mcad");
    public static final MIMEType application_mime = new MIMEType( "application/mime");
    public static final MIMEType application_mspowerpoint = new MIMEType( "application/mspowerpoint");
    public static final MIMEType application_msword = new MIMEType( "application/msword");
    public static final MIMEType application_mswrite = new MIMEType( "application/mswrite");
    public static final MIMEType application_netmc = new MIMEType( "application/netmc");
    public static final MIMEType application_octet_stream = new MIMEType( "application/octet-stream");
    public static final MIMEType application_oda = new MIMEType( "application/oda");
    public static final MIMEType application_pdf = new MIMEType( "application/pdf");
    public static final MIMEType application_pkcs_12 = new MIMEType( "application/pkcs-12");
    public static final MIMEType application_pkcs10 = new MIMEType( "application/pkcs10");
    public static final MIMEType application_pkcs7_mime = new MIMEType( "application/pkcs7-mime");
    public static final MIMEType application_pkcs7_signature = new MIMEType( "application/pkcs7-signature");
    public static final MIMEType application_plain = new MIMEType( "application/plain");
    public static final MIMEType application_postscript = new MIMEType( "application/postscript");
    public static final MIMEType application_powerpoint = new MIMEType( "application/powerpoint");
    public static final MIMEType application_pro_eng = new MIMEType( "application/pro_eng");
    public static final MIMEType application_ringing_tones = new MIMEType( "application/ringing-tones");
    public static final MIMEType application_rtf = new MIMEType( "application/rtf");
    public static final MIMEType application_sdp = new MIMEType( "application/sdp");
    public static final MIMEType application_sea = new MIMEType( "application/sea");
    public static final MIMEType application_set = new MIMEType( "application/set");
    public static final MIMEType application_sla = new MIMEType( "application/sla");
    public static final MIMEType application_smil = new MIMEType( "application/smil");
    public static final MIMEType application_solids = new MIMEType( "application/solids");
    public static final MIMEType application_sounder = new MIMEType( "application/sounder");
    public static final MIMEType application_step = new MIMEType( "application/step");
    public static final MIMEType application_streamingmedia = new MIMEType( "application/streamingmedia");
    public static final MIMEType application_toolbook = new MIMEType( "application/toolbook");
    public static final MIMEType application_vda = new MIMEType( "application/vda");
    public static final MIMEType application_vnd_hp_hpgl = new MIMEType( "application/vnd.hp-hpgl");
    public static final MIMEType application_vnd_hp_pcl = new MIMEType( "application/vnd.hp-pcl");
    public static final MIMEType application_vnd_ms_excel = new MIMEType( "application/vnd.ms-excel");
    public static final MIMEType application_vnd_ms_pki_certstore = new MIMEType( "application/vnd.ms-pki.certstore");
    public static final MIMEType application_vnd_ms_pki_pko = new MIMEType( "application/vnd.ms-pki.pko");
    public static final MIMEType application_vnd_ms_pki_stl = new MIMEType( "application/vnd.ms-pki.stl");
    public static final MIMEType application_vnd_ms_powerpoint = new MIMEType( "application/vnd.ms-powerpoint");
    public static final MIMEType application_vnd_ms_project = new MIMEType( "application/vnd.ms-project");
    public static final MIMEType application_vnd_nokia_configuration_message = new MIMEType( "application/vnd.nokia.configuration-message");
    public static final MIMEType application_vnd_nokia_ringing_tone = new MIMEType( "application/vnd.nokia.ringing-tone");
    public static final MIMEType application_vnd_rn_realmedia = new MIMEType( "application/vnd.rn-realmedia");
    public static final MIMEType application_vnd_rn_realplayer = new MIMEType( "application/vnd.rn-realplayer");
    public static final MIMEType application_vnd_wap_wmlc = new MIMEType( "application/vnd.wap.wmlc");
    public static final MIMEType application_vnd_wap_wmlscriptc = new MIMEType( "application/vnd.wap.wmlscriptc");
    public static final MIMEType application_vnd_xara = new MIMEType( "application/vnd.xara");
    public static final MIMEType application_vocaltec_media_desc = new MIMEType( "application/vocaltec-media-desc");
    public static final MIMEType application_vocaltec_media_file = new MIMEType( "application/vocaltec-media-file");
    public static final MIMEType application_wordperfect = new MIMEType( "application/wordperfect");
    public static final MIMEType application_wordperfect6_0 = new MIMEType( "application/wordperfect6.0");
    public static final MIMEType application_wordperfect6_1 = new MIMEType( "application/wordperfect6.1");
    public static final MIMEType application_x_123 = new MIMEType( "application/x-123");
    public static final MIMEType application_x_binary = new MIMEType( "application/x-binary");
    public static final MIMEType application_x_binhex40 = new MIMEType( "application/x-binhex40");
    public static final MIMEType application_x_bsh = new MIMEType( "application/x-bsh");
    public static final MIMEType application_x_bzip = new MIMEType( "application/x-bzip");
    public static final MIMEType application_x_bzip2 = new MIMEType( "application/x-bzip2");
    public static final MIMEType application_x_cdlink = new MIMEType( "application/x-cdlink");
    public static final MIMEType application_x_cmu_raster = new MIMEType( "application/x-cmu-raster");
    public static final MIMEType application_x_compress = new MIMEType( "application/x-compress");
    public static final MIMEType application_x_compressed = new MIMEType( "application/x-compressed");
    public static final MIMEType application_x_conference = new MIMEType( "application/x-conference");
    public static final MIMEType application_x_dvi = new MIMEType( "application/x-dvi");
    public static final MIMEType application_x_excel = new MIMEType( "application/x-excel");
    public static final MIMEType application_x_frame = new MIMEType( "application/x-frame");
    public static final MIMEType application_x_freelance = new MIMEType( "application/x-freelance");
    public static final MIMEType application_x_gsp = new MIMEType( "application/x-gsp");
    public static final MIMEType application_x_gss = new MIMEType( "application/x-gss");
    public static final MIMEType application_x_gtar = new MIMEType( "application/x-gtar");
    public static final MIMEType application_x_gzip = new MIMEType( "application/x-gzip");
    public static final MIMEType application_x_hdf = new MIMEType( "application/x-hdf");
    public static final MIMEType application_x_helpfile = new MIMEType( "application/x-helpfile");
    public static final MIMEType application_x_httpd_imap = new MIMEType( "application/x-httpd-imap");
    public static final MIMEType application_x_ima = new MIMEType( "application/x-ima");
    public static final MIMEType application_x_internett_signup = new MIMEType( "application/x-internett-signup");
    public static final MIMEType application_x_inventor = new MIMEType( "application/x-inventor");
    public static final MIMEType application_x_ip2 = new MIMEType( "application/x-ip2");
    public static final MIMEType application_x_java_class = new MIMEType( "application/x-java-class");
    public static final MIMEType application_x_java_commerce = new MIMEType( "application/x-java-commerce");
    public static final MIMEType application_x_javascript = new MIMEType( "application/x-javascript");
    public static final MIMEType application_x_koan = new MIMEType( "application/x-koan");
    public static final MIMEType application_x_ksh = new MIMEType( "application/x-ksh");
    public static final MIMEType application_x_latex = new MIMEType( "application/x-latex");
    public static final MIMEType application_x_lha = new MIMEType( "application/x-lha");
    public static final MIMEType application_x_lisp = new MIMEType( "application/x-lisp");
    public static final MIMEType application_x_livescreen = new MIMEType( "application/x-livescreen");
    public static final MIMEType application_x_lotus = new MIMEType( "application/x-lotus");
    public static final MIMEType application_x_lotusscreencam = new MIMEType( "application/x-lotusscreencam");
    public static final MIMEType application_x_lzh = new MIMEType( "application/x-lzh");
    public static final MIMEType application_x_lzx = new MIMEType( "application/x-lzx");
    public static final MIMEType application_x_mac_binhex40 = new MIMEType( "application/x-mac-binhex40");
    public static final MIMEType application_x_macbinary = new MIMEType( "application/x-macbinary");
    public static final MIMEType application_x_magic_cap_package_1_0 = new MIMEType( "application/x-magic-cap-package-1.0");
    public static final MIMEType application_x_mathcad = new MIMEType( "application/x-mathcad");
    public static final MIMEType application_x_meme = new MIMEType( "application/x-meme");
    public static final MIMEType application_x_midi = new MIMEType( "application/x-midi");
    public static final MIMEType application_x_mif = new MIMEType( "application/x-mif");
    public static final MIMEType application_x_mix_transfer = new MIMEType( "application/x-mix-transfer");
    public static final MIMEType application_x_mplayer2 = new MIMEType( "application/x-mplayer2");
    public static final MIMEType application_x_msexcel = new MIMEType( "application/x-msexcel");
    public static final MIMEType application_x_mspowerpoint = new MIMEType( "application/x-mspowerpoint");
    public static final MIMEType application_x_navidoc = new MIMEType( "application/x-navidoc");
    public static final MIMEType application_x_navimap = new MIMEType( "application/x-navimap");
    public static final MIMEType application_x_navistyle = new MIMEType( "application/x-navistyle");
    public static final MIMEType application_x_netcdf = new MIMEType( "application/x-netcdf");
    public static final MIMEType application_x_newton_compatible_pkg = new MIMEType( "application/x-newton-compatible-pkg");
    public static final MIMEType application_x_omc = new MIMEType( "application/x-omc");
    public static final MIMEType application_x_omcdatamaker = new MIMEType( "application/x-omcdatamaker");
    public static final MIMEType application_x_omcregerator = new MIMEType( "application/x-omcregerator");
    public static final MIMEType application_x_pagemaker = new MIMEType( "application/x-pagemaker");
    public static final MIMEType application_x_pcl = new MIMEType( "application/x-pcl");
    public static final MIMEType application_x_pixclscript = new MIMEType( "application/x-pixclscript");
    public static final MIMEType application_x_pkcs10 = new MIMEType( "application/x-pkcs10");
    public static final MIMEType application_x_pkcs12 = new MIMEType( "application/x-pkcs12");
    public static final MIMEType application_x_pkcs7_certificates = new MIMEType( "application/x-pkcs7-certificates");
    public static final MIMEType application_x_pkcs7_certreqresp = new MIMEType( "application/x-pkcs7-certreqresp");
    public static final MIMEType application_x_pkcs7_mime = new MIMEType( "application/x-pkcs7-mime");
    public static final MIMEType application_x_pkcs7_signature = new MIMEType( "application/x-pkcs7-signature");
    public static final MIMEType application_x_portable_anymap = new MIMEType( "application/x-portable-anymap");
    public static final MIMEType application_x_project = new MIMEType( "application/x-project");
    public static final MIMEType application_x_qpro = new MIMEType( "application/x-qpro");
    public static final MIMEType application_x_rtf = new MIMEType( "application/x-rtf");
    public static final MIMEType application_x_sdp = new MIMEType( "application/x-sdp");
    public static final MIMEType application_x_sea = new MIMEType( "application/x-sea");
    public static final MIMEType application_x_seelogo = new MIMEType( "application/x-seelogo");
    public static final MIMEType application_x_sh = new MIMEType( "application/x-sh");
    public static final MIMEType application_x_shar = new MIMEType( "application/x-shar");
    public static final MIMEType application_x_shockwave_flash = new MIMEType( "application/x-shockwave-flash");
    public static final MIMEType application_x_sit = new MIMEType( "application/x-sit");
    public static final MIMEType application_x_sprite = new MIMEType( "application/x-sprite");
    public static final MIMEType application_x_stuffit = new MIMEType( "application/x-stuffit");
    public static final MIMEType application_x_sv4cpio = new MIMEType( "application/x-sv4cpio");
    public static final MIMEType application_x_sv4crc = new MIMEType( "application/x-sv4crc");
    public static final MIMEType application_x_tar = new MIMEType( "application/x-tar");
    public static final MIMEType application_x_tbook = new MIMEType( "application/x-tbook");
    public static final MIMEType application_x_tcl = new MIMEType( "application/x-tcl");
    public static final MIMEType application_x_tex = new MIMEType( "application/x-tex");
    public static final MIMEType application_x_texinfo = new MIMEType( "application/x-texinfo");
    public static final MIMEType application_x_troff = new MIMEType( "application/x-troff");
    public static final MIMEType application_x_troff_man = new MIMEType( "application/x-troff-man");
    public static final MIMEType application_x_troff_me = new MIMEType( "application/x-troff-me");
    public static final MIMEType application_x_troff_ms = new MIMEType( "application/x-troff-ms");
    public static final MIMEType application_x_ustar = new MIMEType( "application/x-ustar");
    public static final MIMEType application_x_visio = new MIMEType( "application/x-visio");
    public static final MIMEType application_x_vnd_audioexplosion_mzz = new MIMEType( "application/x-vnd.audioexplosion.mzz");
    public static final MIMEType application_x_vnd_ls_xpix = new MIMEType( "application/x-vnd.ls-xpix");
    public static final MIMEType application_x_vrml = new MIMEType( "application/x-vrml");
    public static final MIMEType application_x_wais_source = new MIMEType( "application/x-wais-source");
    public static final MIMEType application_x_winhelp = new MIMEType( "application/x-winhelp");
    public static final MIMEType application_x_wintalk = new MIMEType( "application/x-wintalk");
    public static final MIMEType application_x_world = new MIMEType( "application/x-world");
    public static final MIMEType application_x_wpwin = new MIMEType( "application/x-wpwin");
    public static final MIMEType application_x_wri = new MIMEType( "application/x-wri");
    public static final MIMEType application_x_zip_compressed = new MIMEType( "application/x-zip-compressed");
    public static final MIMEType application_xml = new MIMEType( "application/xml");
    public static final MIMEType application_zip = new MIMEType( "application/zip");
    public static final MIMEType audio_aiff = new MIMEType( "audio/aiff");
    public static final MIMEType audio_basic = new MIMEType( "audio/basic");
    public static final MIMEType audio_it = new MIMEType( "audio/it");
    public static final MIMEType audio_make = new MIMEType( "audio/make");
    public static final MIMEType audio_make_my_funk = new MIMEType( "audio/make.my.funk");
    public static final MIMEType audio_mid = new MIMEType( "audio/mid");
    public static final MIMEType audio_midi = new MIMEType( "audio/midi");
    public static final MIMEType audio_mod = new MIMEType( "audio/mod");
    public static final MIMEType audio_mpeg = new MIMEType( "audio/mpeg");
    public static final MIMEType audio_mpeg3 = new MIMEType( "audio/mpeg3");
    public static final MIMEType audio_nspaudio = new MIMEType( "audio/nspaudio");
    public static final MIMEType audio_s3m = new MIMEType( "audio/s3m");
    public static final MIMEType audio_tsp_audio = new MIMEType( "audio/tsp-audio");
    public static final MIMEType audio_tsplayer = new MIMEType( "audio/tsplayer");
    public static final MIMEType audio_vnd_qcelp = new MIMEType( "audio/vnd.qcelp");
    public static final MIMEType audio_voc = new MIMEType( "audio/voc");
    public static final MIMEType audio_voxware = new MIMEType( "audio/voxware");
    public static final MIMEType audio_wav = new MIMEType( "audio/wav");
    public static final MIMEType audio_x_adpcm = new MIMEType( "audio/x-adpcm");
    public static final MIMEType audio_x_aiff = new MIMEType( "audio/x-aiff");
    public static final MIMEType audio_x_au = new MIMEType( "audio/x-au");
    public static final MIMEType audio_x_gsm = new MIMEType( "audio/x-gsm");
    public static final MIMEType audio_x_jam = new MIMEType( "audio/x-jam");
    public static final MIMEType audio_x_liveaudio = new MIMEType( "audio/x-liveaudio");
    public static final MIMEType audio_x_mid = new MIMEType( "audio/x-mid");
    public static final MIMEType audio_x_midi = new MIMEType( "audio/x-midi");
    public static final MIMEType audio_x_mod = new MIMEType( "audio/x-mod");
    public static final MIMEType audio_x_mpeg = new MIMEType( "audio/x-mpeg");
    public static final MIMEType audio_x_mpeg_3 = new MIMEType( "audio/x-mpeg-3");
    public static final MIMEType audio_x_mpequrl = new MIMEType( "audio/x-mpequrl");
    public static final MIMEType audio_x_nspaudio = new MIMEType( "audio/x-nspaudio");
    public static final MIMEType audio_x_pn_realaudio = new MIMEType( "audio/x-pn-realaudio");
    public static final MIMEType audio_x_pn_realaudio_plugin = new MIMEType( "audio/x-pn-realaudio-plugin");
    public static final MIMEType audio_x_psid = new MIMEType( "audio/x-psid");
    public static final MIMEType audio_x_realaudio = new MIMEType( "audio/x-realaudio");
    public static final MIMEType audio_x_twinvq = new MIMEType( "audio/x-twinvq");
    public static final MIMEType audio_x_twinvq_plugin = new MIMEType( "audio/x-twinvq-plugin");
    public static final MIMEType audio_x_vnd_audioexplosion_mjuicemediafile = new MIMEType( "audio/x-vnd.audioexplosion.mjuicemediafile");
    public static final MIMEType audio_x_voc = new MIMEType( "audio/x-voc");
    public static final MIMEType audio_x_wav = new MIMEType( "audio/x-wav");
    public static final MIMEType audio_xm = new MIMEType( "audio/xm");
    public static final MIMEType chemical_x_pdb = new MIMEType( "chemical/x-pdb");
    public static final MIMEType i_world_i_vrml = new MIMEType( "i-world/i-vrml");
    public static final MIMEType image_bmp = new MIMEType( "image/bmp");
    public static final MIMEType image_cmu_raster = new MIMEType( "image/cmu-raster");
    public static final MIMEType image_florian = new MIMEType( "image/florian");
    public static final MIMEType image_gif = new MIMEType( "image/gif");
    public static final MIMEType image_ief = new MIMEType( "image/ief");
    public static final MIMEType image_jpeg = new MIMEType( "image/jpeg");
    public static final MIMEType image_jutvision = new MIMEType( "image/jutvision");
    public static final MIMEType image_naplps = new MIMEType( "image/naplps");
    public static final MIMEType image_pict = new MIMEType( "image/pict");
    public static final MIMEType image_pjpeg = new MIMEType( "image/pjpeg");
    public static final MIMEType image_png = new MIMEType( "image/png");
    public static final MIMEType image_tiff = new MIMEType( "image/tiff");
    public static final MIMEType image_vasa = new MIMEType( "image/vasa");
    public static final MIMEType image_vnd_dwg = new MIMEType( "image/vnd.dwg");
    public static final MIMEType image_vnd_rn_realflash = new MIMEType( "image/vnd.rn-realflash");
    public static final MIMEType image_vnd_rn_realpix = new MIMEType( "image/vnd.rn-realpix");
    public static final MIMEType image_vnd_wap_wbmp = new MIMEType( "image/vnd.wap.wbmp");
    public static final MIMEType image_vnd_xiff = new MIMEType( "image/vnd.xiff");
    public static final MIMEType image_x_cmu_raster = new MIMEType( "image/x-cmu-raster");
    public static final MIMEType image_x_dwg = new MIMEType( "image/x-dwg");
    public static final MIMEType image_x_icon = new MIMEType( "image/x-icon");
    public static final MIMEType image_x_jg = new MIMEType( "image/x-jg");
    public static final MIMEType image_x_jps = new MIMEType( "image/x-jps");
    public static final MIMEType image_x_niff = new MIMEType( "image/x-niff");
    public static final MIMEType image_x_pcx = new MIMEType( "image/x-pcx");
    public static final MIMEType image_x_pict = new MIMEType( "image/x-pict");
    public static final MIMEType image_x_portable_anymap = new MIMEType( "image/x-portable-anymap");
    public static final MIMEType image_x_portable_bitmap = new MIMEType( "image/x-portable-bitmap");
    public static final MIMEType image_x_portable_graymap = new MIMEType( "image/x-portable-graymap");
    public static final MIMEType image_x_portable_greymap = new MIMEType( "image/x-portable-greymap");
    public static final MIMEType image_x_portable_pixmap = new MIMEType( "image/x-portable-pixmap");
    public static final MIMEType image_x_quicktime = new MIMEType( "image/x-quicktime");
    public static final MIMEType image_x_rgb = new MIMEType( "image/x-rgb");
    public static final MIMEType image_x_tiff = new MIMEType( "image/x-tiff");
    public static final MIMEType image_x_windows_bmp = new MIMEType( "image/x-windows-bmp");
    public static final MIMEType image_x_xbitmap = new MIMEType( "image/x-xbitmap");
    public static final MIMEType image_x_xbm = new MIMEType( "image/x-xbm");
    public static final MIMEType image_x_xpixmap = new MIMEType( "image/x-xpixmap");
    public static final MIMEType image_x_xwd = new MIMEType( "image/x-xwd");
    public static final MIMEType image_x_xwindowdump = new MIMEType( "image/x-xwindowdump");
    public static final MIMEType image_xbm = new MIMEType( "image/xbm");
    public static final MIMEType image_xpm = new MIMEType( "image/xpm");
    public static final MIMEType message_rfc822 = new MIMEType( "message/rfc822");
    public static final MIMEType model_iges = new MIMEType( "model/iges");
    public static final MIMEType model_vrml = new MIMEType( "model/vrml");
    public static final MIMEType model_x_pov = new MIMEType( "model/x-pov");
    public static final MIMEType multipart_x_gzip = new MIMEType( "multipart/x-gzip");
    public static final MIMEType multipart_x_ustar = new MIMEType( "multipart/x-ustar");
    public static final MIMEType multipart_x_zip = new MIMEType( "multipart/x-zip");
    public static final MIMEType music_crescendo = new MIMEType( "music/crescendo");
    public static final MIMEType music_x_karaoke = new MIMEType( "music/x-karaoke");
    public static final MIMEType paleovu_x_pv = new MIMEType( "paleovu/x-pv");
    public static final MIMEType text_asp = new MIMEType( "text/asp");
    public static final MIMEType text_css = new MIMEType( "text/css");
    public static final MIMEType text_html = new MIMEType( "text/html");
    public static final MIMEType text_mcf = new MIMEType( "text/mcf");
    public static final MIMEType text_pascal = new MIMEType( "text/pascal");
    public static final MIMEType text_plain = new MIMEType( "text/plain");
    public static final MIMEType text_richtext = new MIMEType( "text/richtext");
    public static final MIMEType text_scriplet = new MIMEType( "text/scriplet");
    public static final MIMEType text_sgml = new MIMEType( "text/sgml");
    public static final MIMEType text_tab_separated_values = new MIMEType( "text/tab-separated-values");
    public static final MIMEType text_uri_list = new MIMEType( "text/uri-list");
    public static final MIMEType text_vnd_rn_realtext = new MIMEType( "text/vnd.rn-realtext");
    public static final MIMEType text_vnd_wap_wml = new MIMEType( "text/vnd.wap.wml");
    public static final MIMEType text_vnd_wap_wmlscript = new MIMEType( "text/vnd.wap.wmlscript");
    public static final MIMEType text_webviewhtml = new MIMEType( "text/webviewhtml");
    public static final MIMEType text_x_asm = new MIMEType( "text/x-asm");
    public static final MIMEType text_x_c = new MIMEType( "text/x-c");
    public static final MIMEType text_x_component = new MIMEType( "text/x-component");
    public static final MIMEType text_x_h = new MIMEType( "text/x-h");
    public static final MIMEType text_x_java_source = new MIMEType( "text/x-java-source");
    public static final MIMEType text_x_la_asf = new MIMEType( "text/x-la-asf");
    public static final MIMEType text_x_m = new MIMEType( "text/x-m");
    public static final MIMEType text_x_pascal = new MIMEType( "text/x-pascal");
    public static final MIMEType text_x_script = new MIMEType( "text/x-script");
    public static final MIMEType text_x_script_guile = new MIMEType( "text/x-script.guile");
    public static final MIMEType text_x_script_ksh = new MIMEType( "text/x-script.ksh");
    public static final MIMEType text_x_script_lisp = new MIMEType( "text/x-script.lisp");
    public static final MIMEType text_x_script_perl = new MIMEType( "text/x-script.perl");
    public static final MIMEType text_x_script_perl_module = new MIMEType( "text/x-script.perl-module");
    public static final MIMEType text_x_script_phyton = new MIMEType( "text/x-script.phyton");
    public static final MIMEType text_x_script_rexx = new MIMEType( "text/x-script.rexx");
    public static final MIMEType text_x_script_scheme = new MIMEType( "text/x-script.scheme");
    public static final MIMEType text_x_script_sh = new MIMEType( "text/x-script.sh");
    public static final MIMEType text_x_script_tcl = new MIMEType( "text/x-script.tcl");
    public static final MIMEType text_x_script_tcsh = new MIMEType( "text/x-script.tcsh");
    public static final MIMEType text_x_script_zsh = new MIMEType( "text/x-script.zsh");
    public static final MIMEType text_x_server_parsed_html = new MIMEType( "text/x-server-parsed-html");
    public static final MIMEType text_x_sgml = new MIMEType( "text/x-sgml");
    public static final MIMEType text_x_speech = new MIMEType( "text/x-speech");
    public static final MIMEType text_x_uil = new MIMEType( "text/x-uil");
    public static final MIMEType text_x_uuencode = new MIMEType( "text/x-uuencode");
    public static final MIMEType text_x_vcalendar = new MIMEType( "text/x-vcalendar");
    public static final MIMEType text_xml = new MIMEType( "text/xml");
    public static final MIMEType video_avi = new MIMEType( "video/avi");
    public static final MIMEType video_avs_video = new MIMEType( "video/avs-video");
    public static final MIMEType video_dl = new MIMEType( "video/dl");
    public static final MIMEType video_fli = new MIMEType( "video/fli");
    public static final MIMEType video_gl = new MIMEType( "video/gl");
    public static final MIMEType video_mpeg = new MIMEType( "video/mpeg");
    public static final MIMEType video_msvideo = new MIMEType( "video/msvideo");
    public static final MIMEType video_quicktime = new MIMEType( "video/quicktime");
    public static final MIMEType video_vdo = new MIMEType( "video/vdo");
    public static final MIMEType video_vivo = new MIMEType( "video/vivo");
    public static final MIMEType video_vnd_rn_realvideo = new MIMEType( "video/vnd.rn-realvideo");
    public static final MIMEType video_vnd_vivo = new MIMEType( "video/vnd.vivo");
    public static final MIMEType video_vosaic = new MIMEType( "video/vosaic");
    public static final MIMEType video_x_amt_demorun = new MIMEType( "video/x-amt-demorun");
    public static final MIMEType video_x_amt_showrun = new MIMEType( "video/x-amt-showrun");
    public static final MIMEType video_x_dl = new MIMEType( "video/x-dl");
    public static final MIMEType video_x_dv = new MIMEType( "video/x-dv");
    public static final MIMEType video_x_fli = new MIMEType( "video/x-fli");
    public static final MIMEType video_x_gl = new MIMEType( "video/x-gl");
    public static final MIMEType video_x_isvideo = new MIMEType( "video/x-isvideo");
    public static final MIMEType video_x_motion_jpeg = new MIMEType( "video/x-motion-jpeg");
    public static final MIMEType video_x_mpeg = new MIMEType( "video/x-mpeg");
    public static final MIMEType video_x_mpeq2a = new MIMEType( "video/x-mpeq2a");
    public static final MIMEType video_x_ms_asf = new MIMEType( "video/x-ms-asf");
    public static final MIMEType video_x_msvideo = new MIMEType( "video/x-msvideo");
    public static final MIMEType video_x_qtc = new MIMEType( "video/x-qtc");
    public static final MIMEType video_x_scm = new MIMEType( "video/x-scm");
    public static final MIMEType video_x_sgi_movie = new MIMEType( "video/x-sgi-movie");
    public static final MIMEType windows_metafile = new MIMEType( "windows/metafile");
    public static final MIMEType www_mime = new MIMEType( "www/mime");
    public static final MIMEType x_conference_x_cooltalk = new MIMEType( "x-conference/x-cooltalk");
    public static final MIMEType x_music_x_midi = new MIMEType( "x-music/x-midi");
    public static final MIMEType x_world_x_3dmf = new MIMEType( "x-world/x-3dmf");
    public static final MIMEType x_world_x_svr = new MIMEType( "x-world/x-svr");
    public static final MIMEType x_world_x_vrml = new MIMEType( "x-world/x-vrml");
    public static final MIMEType x_world_x_vrt = new MIMEType( "x-world/x-vrt");
    public static final MIMEType xgl_drawing = new MIMEType( "xgl/drawing");
    public static final MIMEType xgl_movie = new MIMEType( "xgl/movie");

    public String type;

    public MIMEType( String type )
    {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals( Object other )
    {
        return other instanceof MIMEType && ((MIMEType) other).type.equals(type);
    }

    @Override
    public int hashCode()
    {
        return type.hashCode();
    }
}