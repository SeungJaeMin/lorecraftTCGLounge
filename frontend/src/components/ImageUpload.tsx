import React, { useState, useEffect, useCallback } from 'react';

interface ImageData {
  imageId: number;
  cardId: number;
  imageName: string;
  imageType: string;
  imageSize: number;
  imageCategory: string;
  uploadedAt: string;
}

interface ImageUploadProps {
  cardId: number;
  cardName: string;
}

const ImageUpload: React.FC<ImageUploadProps> = ({ cardId, cardName }) => {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [uploadResult, setUploadResult] = useState<string>('');
  const [existingImages, setExistingImages] = useState<ImageData[]>([]);
  const [loading, setLoading] = useState(true);

  // 기존 이미지 로드
  const loadExistingImages = useCallback(async () => {
    try {
      const response = await fetch(`http://localhost:8090/api/v1/cards/${cardId}/images`);
      if (response.ok) {
        const images = await response.json();
        setExistingImages(images);
      }
    } catch (error) {
      console.error('Error loading existing images:', error);
    } finally {
      setLoading(false);
    }
  }, [cardId]);

  useEffect(() => {
    loadExistingImages();
  }, [loadExistingImages]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  // 이미지 삭제
  const handleDeleteImage = async (imageId: number) => {
    if (!window.confirm('이 이미지를 삭제하시겠습니까?')) return;

    try {
      const response = await fetch(`http://localhost:8090/api/v1/cards/images/${imageId}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        setUploadResult('✅ 이미지 삭제 성공');
        loadExistingImages(); // 목록 새로고침
      } else {
        setUploadResult('❌ 이미지 삭제 실패');
      }
    } catch (error) {
      console.error('Delete error:', error);
      setUploadResult(`❌ 삭제 에러: ${error}`);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      alert('파일을 선택해주세요');
      return;
    }

    setUploading(true);
    setUploadResult('');

    const formData = new FormData();
    formData.append('file', file);
    formData.append('category', 'main');

    try {
      const response = await fetch(`http://localhost:8090/api/v1/cards/${cardId}/images`, {
        method: 'POST',
        body: formData,
      });

      const result = await response.text();
      
      if (response.ok) {
        setUploadResult(`✅ 업로드 성공: ${result}`);
        setFile(null);
        // 파일 input 초기화
        const fileInput = document.getElementById(`file-${cardId}`) as HTMLInputElement;
        if (fileInput) fileInput.value = '';
        // 기존 이미지 목록 새로고침
        loadExistingImages();
      } else {
        setUploadResult(`❌ 업로드 실패: ${response.status} - ${result}`);
      }
    } catch (error) {
      console.error('Upload error:', error);
      setUploadResult(`❌ 업로드 에러: ${error}`);
    } finally {
      setUploading(false);
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="image-upload-card">
        <h3>📸 {cardName} (카드 ID: {cardId})</h3>
        <p>이미지 로딩 중...</p>
      </div>
    );
  }

  return (
    <div className="image-upload-card">
      <h3>📸 {cardName} (카드 ID: {cardId})</h3>
      
      {/* 기존 이미지 목록 */}
      <div className="existing-images">
        <h4>📂 기존 이미지 ({existingImages.length}개)</h4>
        {existingImages.length === 0 ? (
          <p className="no-images">업로드된 이미지가 없습니다.</p>
        ) : (
          <div className="image-list">
            {existingImages.map((image, index) => (
              <div key={image.imageId} className="image-item">
                <div className="image-info">
                  <span className="image-number">#{index + 1}</span>
                  <div className="image-details">
                    <div className="image-name">{image.imageName}</div>
                    <div className="image-meta">
                      {image.imageType} • {formatFileSize(image.imageSize)} • {formatDate(image.uploadedAt)}
                    </div>
                  </div>
                </div>
                <div className="image-actions">
                  <a 
                    href={`http://localhost:8090/api/v1/cards/images/${image.imageId}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="view-button"
                    title="이미지 보기"
                  >
                    👁️
                  </a>
                  <button 
                    onClick={() => handleDeleteImage(image.imageId)}
                    className="delete-button"
                    title="이미지 삭제"
                  >
                    🗑️
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* 새 이미지 업로드 */}
      <div className="upload-form">
        <h4>📤 새 이미지 업로드</h4>
        <div className="form-group">
          <label htmlFor={`file-${cardId}`}>이미지 파일:</label>
          <input
            id={`file-${cardId}`}
            type="file"
            accept="image/*"
            onChange={handleFileChange}
          />
        </div>

        {file && (
          <div className="file-preview">
            <p>선택된 파일: <strong>{file.name}</strong> ({formatFileSize(file.size)})</p>
          </div>
        )}

        <button 
          onClick={handleUpload} 
          disabled={uploading || !file}
          className="upload-button"
        >
          {uploading ? '업로드 중...' : '업로드'}
        </button>
      </div>

      {uploadResult && (
        <div className={`result ${uploadResult.includes('✅') ? 'success' : 'error'}`}>
          <pre>{uploadResult}</pre>
        </div>
      )}

    </div>
  );
};

export default ImageUpload;