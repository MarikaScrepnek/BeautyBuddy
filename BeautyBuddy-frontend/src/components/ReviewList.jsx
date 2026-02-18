import { useEffect, useState } from "react";

import { deleteReview, getReviews, upvoteReview } from "../api/reviewApi";
import { getCurrentUser } from "../api/authApi";

import Toast from "./Toast";

import "./ReviewList.css";

const normalizeReviews = (data) => {
	if (Array.isArray(data)) return data;
	if (Array.isArray(data?.content)) return data.content;
	return [];
};

const formatDate = (value) => {
	if (!value) return "";
	const date = new Date(value);
	if (Number.isNaN(date.getTime())) return String(value);
	return date.toLocaleDateString(undefined, {
		year: "numeric",
		month: "short",
		day: "numeric",
	});
};

const getRatingNumber = (rating) => {
	const num = Number(rating);
	return Number.isFinite(num) ? num : 0;
};

const getReviewId = (review) => review?.reviewId ?? review?.id ?? null;

const StarDisplay = ({ rating }) => {
	const safeRating = getRatingNumber(rating);
	return (
		<div className="review-stars" aria-label={`Rating ${safeRating} out of 5`}>
			{[1, 2, 3, 4, 5].map((index) => {
				const fillPct = Math.min(
					100,
					Math.max(0, (safeRating - (index - 1)) * 100)
				);

				return (
					<span
						key={index}
						className="review-star"
						style={{ "--fill": `${fillPct}%` }}
						aria-hidden="true"
					>
						<svg viewBox="0 0 24 24">
							<path
								className="review-star-outline"
								d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"
							/>
							<g className="review-star-fill">
								<path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
							</g>
						</svg>
					</span>
				);
			})}
			<span className="review-rating-value">{safeRating.toFixed(1)}</span>
		</div>
	);
};

export default function ReviewList({ productId, refreshKey, onEditReview }) {
	const [reviews, setReviews] = useState([]);
	const [loading, setLoading] = useState(true);
	const [currentUser, setCurrentUser] = useState(null);
	const [actionMessage, setActionMessage] = useState("");
	const [pendingId, setPendingId] = useState(null);
	const [upvotedIds, setUpvotedIds] = useState(() => new Set());
    const [toast, setToast] = useState({ message: "", type: "info" });

	useEffect(() => {
		getCurrentUser()
			.then(setCurrentUser)
			.catch(() => setCurrentUser(null));
	}, []);

	useEffect(() => {
		if (!productId) return;
		let isMounted = true;

		setLoading(true);
		setToast({ message: "", type: "info" });

		getReviews(productId)
			.then((data) => {
				if (!isMounted) return;
				const nextReviews = normalizeReviews(data);
				setReviews(nextReviews);
				setUpvotedIds(
					new Set(
						nextReviews
							.filter((review) => review?.hasUpvoted)
							.map((review) => getReviewId(review))
							.filter(Boolean)
					)
				);
			})
			.catch(() => {
				if (!isMounted) return;
				setToast({ message: "Failed to load reviews.", type: "error" });
			})
			.finally(() => {
				if (!isMounted) return;
				setLoading(false);
			});

		return () => {
			isMounted = false;
		};
	}, [productId, refreshKey]);

	const handleUpvote = async (review) => {
		setActionMessage("");

		if (!currentUser) {
			setActionMessage("Log in to mark reviews as helpful.");
			return;
		}

		const reviewId = getReviewId(review);
		if (!reviewId) {
			setActionMessage("Helpful votes are unavailable for this review.");
			return;
		}
		if (upvotedIds.has(reviewId)) {
			setActionMessage("You already marked this as helpful.");
			return;
		}

		setPendingId(reviewId);
		const success = await upvoteReview(reviewId);
		setPendingId(null);
		if (!success) {
			setActionMessage("Unable to mark as helpful right now.");
		} else {
			setReviews((items) =>
				items.map((item) => {
					if (getReviewId(item) !== reviewId) return item;
					const nextCount = Number(item?.upvoteCount ?? 0) + 1;
					return { ...item, upvoteCount: nextCount };
				})
			);
			setUpvotedIds((prev) => {
				const next = new Set(prev);
				next.add(reviewId);
				return next;
			});
			setToast({ message: "Thanks for the feedback!", type: "success" });
		}
	};

	const handleDelete = async (review) => {
		setActionMessage("");

		const reviewId = getReviewId(review);
		if (!reviewId) {
			setToast({ message: "Unable to delete this review.", type: "error" });
			return;
		}

		setPendingId(reviewId);
		const success = await deleteReview(reviewId);
		setPendingId(null);
		if (!success) {
			setToast({ message: "Unable to delete this review right now.", type: "error" });
			return;
		}

		setReviews((items) => items.filter((item) => getReviewId(item) !== reviewId));
	};

	if (loading) {
		return <p className="review-loading">Loading reviews...</p>;
	}

	if (toast.message) {
		return <Toast message={toast.message} type={toast.type} onClose={() => setToast({ message: "", type: "info" })} />;
	}

	if (!reviews.length) {
		return (
			<div className="review-empty">
				<p>No reviews have been submitted about this product yet.</p>
			</div>
		);
	}

	return (
		<div className="review-list">
			{actionMessage ? <p className="review-message">{actionMessage}</p> : null}
			{reviews.map((review) => {
				const reviewId = getReviewId(review);
				const reviewerName = review?.reviewerName ?? "Anonymous";
				const reviewTitle = review?.reviewTitle ?? review?.title ?? "";
				const reviewText = review?.reviewText ?? review?.text ?? "";
				const reviewDate = formatDate(review?.createdAt);
				const isOwner =
					currentUser?.username && currentUser.username === reviewerName;
				const avatar = review?.reviewerProfilePicture;
				const isPending = Boolean(reviewId) && pendingId === reviewId;
				const isUpvoted = Boolean(reviewId) && upvotedIds.has(reviewId);
				const upvoteCount = Number(review?.upvoteCount ?? 0);

				return (
					<article key={reviewId ?? `${reviewerName}-${reviewDate}`} className="review-card">
						<header className="review-header">
							<div className="reviewer">
								<div className="review-avatar">
									{avatar ? (
										<img src={avatar} alt={reviewerName} />
									) : (
										<span>{reviewerName.slice(0, 1).toUpperCase()}</span>
									)}
								</div>
								<div>
									<div className="reviewer-name">{reviewerName}</div>
									{review?.shadeName ? (
										<div className="review-shade">Shade: {review.shadeName}</div>
									) : null}
								</div>
							</div>
							<div className="review-meta">
								<StarDisplay rating={review?.rating} />
								{reviewDate ? <span className="review-date">{reviewDate}</span> : null}
							</div>
						</header>

						{reviewTitle ? <h3 className="review-title">{reviewTitle}</h3> : null}
						{reviewText ? <p className="review-text">{reviewText}</p> : null}

						<div className="review-actions">
							{isOwner ? (
								<>
									<button
										type="button"
										className="review-action-btn"
										onClick={() =>
											typeof onEditReview === "function"
												? onEditReview(review)
												: setActionMessage("Edit isn't available yet.")
										}
										disabled={isPending}
									>
										Edit
									</button>
									<button
										type="button"
										className="review-action-btn review-action-danger"
										onClick={() => handleDelete(review)}
										disabled={isPending}
									>
										Delete
									</button>
								</>
							) : (
								<>
									<button
										type="button"
										className="review-action-btn"
										onClick={() => handleUpvote(review)}
										disabled={isPending || isUpvoted}
									>
										{isUpvoted ? "Helpful ✓" : "Helpful"}
									</button>
									<span className="review-upvote-count">
										{upvoteCount} helpful vote{upvoteCount !== 1 ? "s" : ""}
									</span>
								</>
							)}
						</div>
					</article>
				);
			})}
		</div>
	);
}
