import { useEffect, useState } from "react";

import { deleteReview, getReviews, upvoteReview, removeUpvoteReview, reportReview } from "../api/reviewApi";
import { getCurrentUser } from "../../auth/api/authApi";

import Toast from "../../../components/ui/Toast";
import ReviewCard from "./ReviewCard";
import ReportModal from "../../report/modals/ReportModal";

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
	return date.toLocaleString(undefined, {
		year: "numeric",
		month: "short",
		day: "numeric",
		hour: "numeric",
		minute: "2-digit",
	});
};

const getReviewId = (review) => review?.reviewId ?? review?.id ?? null;

export default function ReviewList({ productId, refreshKey, onEditReview, onRequireLogin, sortKey, shadeFilter }) {
	const [reviews, setReviews] = useState([]);
	const [loading, setLoading] = useState(true);
	const [currentUser, setCurrentUser] = useState(null);
	const [actionMessage, setActionMessage] = useState("");
	const [pendingId, setPendingId] = useState(null);
	const [upvotedIds, setUpvotedIds] = useState(() => new Set());
    const [toast, setToast] = useState({ message: "", type: "info" });
	const [reportOpen, setReportOpen] = useState(false);
	const [reportTarget, setReportTarget] = useState(null);

	const applyReviews = (data) => {
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
	};

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

		getReviews(productId, 0, 10, { sort: sortKey, filter: shadeFilter })
			.then((data) => {
				if (!isMounted) return;
				applyReviews(data);
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
	}, [productId, refreshKey, sortKey, shadeFilter]);

	useEffect(() => {
		if (!productId) return;

		const handleAuthLogin = () => {
			getCurrentUser()
				.then(setCurrentUser)
				.catch(() => setCurrentUser(null));
			getReviews(productId, 0, 10, { sort: sortKey, filter: shadeFilter })
				.then(applyReviews)
				.catch(() =>
					setToast({ message: "Failed to load reviews.", type: "error" })
				);
		};

		const handleAuthLogout = () => {
			setCurrentUser(null);
			setUpvotedIds(new Set());
		};

		window.addEventListener("auth:login", handleAuthLogin);
		window.addEventListener("auth:logout", handleAuthLogout);
		return () => {
			window.removeEventListener("auth:login", handleAuthLogin);
			window.removeEventListener("auth:logout", handleAuthLogout);
		};
	}, [productId, sortKey, shadeFilter]);

	const handleUpvote = async (review) => {
		setActionMessage("");

		if (!currentUser) {
			onRequireLogin?.();
			return;
		}

		const reviewId = getReviewId(review);
		if (!reviewId) {
			setActionMessage("Helpful votes are unavailable for this review.");
			return;
		}
		const isUpvoted = upvotedIds.has(reviewId);
		setPendingId(reviewId);
		const success = isUpvoted
			? await removeUpvoteReview(reviewId)
			: await upvoteReview(reviewId);
		setPendingId(null);
		if (!success) {
			setActionMessage(
				isUpvoted
					? "Unable to remove your helpful vote right now."
					: "Unable to mark as helpful right now."
			);
		} else {
			setReviews((items) =>
				items.map((item) => {
					if (getReviewId(item) !== reviewId) return item;
					const delta = isUpvoted ? -1 : 1;
					const nextCount = Math.max(0, Number(item?.upvoteCount ?? 0) + delta);
					return { ...item, upvoteCount: nextCount };
				})
			);
			setUpvotedIds((prev) => {
				const next = new Set(prev);
				if (isUpvoted) {
					next.delete(reviewId);
				} else {
					next.add(reviewId);
				}
				return next;
			});
			setToast({
				message: isUpvoted ? "Helpful vote removed." : "Thanks for the feedback!",
				type: "success",
			});
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

	const handleReport = async (review) => {
		setActionMessage("");

		if (!currentUser) {
			onRequireLogin?.();
			return;
		}

		setReportTarget(review);
		setReportOpen(true);
	};

	const submitReport = async (reason) => {
		const reviewId = getReviewId(reportTarget);
		if (!reviewId) {
			setToast({ message: "Unable to report this review.", type: "error" });
			return;
		}

		setPendingId(reviewId);
		const success = await reportReview(reviewId, reason);
		setPendingId(null);
		if (!success) {
			setToast({ message: "Unable to report this review right now.", type: "error" });
			return;
		}

		setReportOpen(false);
		setReportTarget(null);
		setReviews((items) => items.filter((item) => getReviewId(item) !== reviewId));
		setToast({ message: "Report submitted. Thanks for the feedback.", type: "success" });
	};

	if (loading) {
		return <p className="review-loading">Loading reviews...</p>;
	}

	if (!reviews.length) {
		return (
			<div className="review-empty">
				{toast.message ? (
					<Toast
						message={toast.message}
						type={toast.type}
						onClose={() => setToast({ message: "", type: "info" })}
					/>
				) : null}
				<p>No reviews have been submitted about this product yet.</p>
			</div>
		);
	}

	return (
		<div className="review-list">
			<ReportModal
				isOpen={reportOpen}
				onClose={() => {
					setReportOpen(false);
					setReportTarget(null);
				}}
				reviewerName={reportTarget?.reviewerName}
				onSubmit={async ({ reason }) => {
					await submitReport(reason);
				}}
			/>
			{toast.message ? (
				<Toast
					message={toast.message}
					type={toast.type}
					onClose={() => setToast({ message: "", type: "info" })}
				/>
			) : null}
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
					<ReviewCard
						key={reviewId ?? `${reviewerName}-${reviewDate}`}
						review={review}
						reviewId={reviewId}
						reviewerName={reviewerName}
						reviewTitle={reviewTitle}
						reviewText={reviewText}
						reviewDate={reviewDate}
						isOwner={isOwner}
						avatar={avatar}
						isPending={isPending}
						isUpvoted={isUpvoted}
						upvoteCount={upvoteCount}
						isLoggedIn={Boolean(currentUser)}
						onRequireLogin={onRequireLogin}
						onEdit={
							typeof onEditReview === "function"
								? onEditReview
								: () => setActionMessage("Edit isn't available yet.")
						}
						onDelete={handleDelete}
						onUpvote={handleUpvote}
						onReport={handleReport}
					/>
				);
			})}
		</div>
	);
}
